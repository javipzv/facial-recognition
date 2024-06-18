package es.upm.ejemplo;

import java.awt.BorderLayout;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JFrameEjemplo extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private BufferedImage uploadedImage = null;
    private List<String> suggestions;
    public AgenteGUI agent;
    public String path;
    private int brightness = 116;
    private boolean increasing = true;
    public List<String> nombres = new ArrayList<String>();
    private JButton searchButton;
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                JFrameEjemplo frame = new JFrameEjemplo();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void cargarSugerenciasDesdeJson() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("json/galeria.json")));
            JSONObject json = new JSONObject(content);
            JSONArray galeria = json.getJSONArray("galeria");

            for (int i = 0; i < galeria.length(); i++) {
                JSONObject item = galeria.getJSONObject(i);
                String nombre = item.getString("nombre");
                suggestions.add(nombre);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    

    public JFrameEjemplo() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel(new GridBagLayout());
        contentPane.setBackground(new Color(245, 245, 245));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
     // Initialize suggestions list and load from JSON
        suggestions = new ArrayList<>();
        cargarSugerenciasDesdeJson();

        mostrarPantallaInicio();
    }

private void mostrarPantallaInicio() {
    contentPane.removeAll();
    contentPane.revalidate();
    contentPane.repaint();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 10, 5, 10); // Reducir espacio vertical
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.weighty = 0.1;

    JLabel title = new JLabel("Reconocimiento facial", SwingConstants.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    contentPane.add(title, gbc);

    gbc.gridy++;
    gbc.insets = new Insets(5, 10, 20, 10); // Reducir espacio entre título y subtítulo
    JLabel subtitle = new JLabel(
        "<html><div style='text-align: center;'>Identifica y nombra rostros en tus imágenes. <br>" +
        "Filtra fotos fácilmente por personas reconocidas para una mejor organización y búsqueda.</div></html>",
        SwingConstants.CENTER
    );
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    contentPane.add(subtitle, gbc);

    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.gridx = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.weightx = 0.5;
    gbc.weighty = 0.1;

    JButton btnSubirImagen = new JButton("Subir Imagen");
    btnSubirImagen.setBackground(new Color(0, 122, 204));
    btnSubirImagen.setForeground(Color.WHITE);
    btnSubirImagen.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    btnSubirImagen.setFocusPainted(false);
    btnSubirImagen.setBorder(BorderFactory.createEmptyBorder());
    btnSubirImagen.setPreferredSize(new Dimension(250, 75));
    btnSubirImagen.addActionListener(e -> subirImagen());
    contentPane.add(btnSubirImagen, gbc);

    gbc.gridy++;
    JButton btnHacerBusqueda = new JButton("Hacer Búsqueda");
    btnHacerBusqueda.setBackground(new Color(0, 122, 204));
    btnHacerBusqueda.setForeground(Color.WHITE);
    btnHacerBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    btnHacerBusqueda.setFocusPainted(false);
    btnHacerBusqueda.setBorder(BorderFactory.createEmptyBorder());
    btnHacerBusqueda.setPreferredSize(new Dimension(250, 75)); // Botones más grandes
    btnHacerBusqueda.addActionListener(e -> hacerBusqueda());
    contentPane.add(btnHacerBusqueda, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.weighty = 0.1;

    try {
        BufferedImage logoImage = ImageIO.read(getClass().getResource("/resources/logo.png"));
        ImageIcon logoIcon = new ImageIcon(logoImage.getScaledInstance(97, 76, Image.SCALE_SMOOTH));
        JLabel logoLabel = new JLabel(logoIcon);
        contentPane.add(logoLabel, gbc);
    } catch (IOException ex) {
        System.err.println("No se pudo cargar la imagen del logo: " + ex.getMessage());
    }

    contentPane.revalidate();
    contentPane.repaint();
}

    private void subirImagen() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName().toLowerCase();
            path = selectedFile.toString();
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".bmp")) {
                try {
                    uploadedImage = ImageIO.read(selectedFile);
                    mostrarPantallaImagenSubida(uploadedImage);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al cargar la imagen", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Formato no válido. Por favor, suba una imagen en formato JPG, JPEG, PNG o BMP.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

 private void mostrarPantallaImagenSubida(BufferedImage image) {
    contentPane.removeAll();
    contentPane.revalidate();
    contentPane.repaint();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(10, 50, 10, 50);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.weightx = 1.0;
    gbc.weighty = 0.1;
    gbc.anchor = GridBagConstraints.NORTH;

    crearBotonVolver(contentPane, this::mostrarPantallaInicio);

    JLabel lblImagenSubida = new JLabel("Imagen subida", SwingConstants.CENTER);
    lblImagenSubida.setFont(new Font("Segoe UI", Font.BOLD, 18));
    contentPane.add(lblImagenSubida, gbc);

    gbc.gridy++;
    gbc.weighty = 0.8;

    ImagePanel imagePanel = new ImagePanel(image);
    contentPane.add(imagePanel, gbc);

    gbc.gridy++;
    gbc.weighty = 0.1;

    JButton btnReconocer = new JButton("Reconocer");
    btnReconocer.setBackground(new Color(0, 122, 204));
    btnReconocer.setForeground(Color.WHITE);
    btnReconocer.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    btnReconocer.setFocusPainted(false);
    btnReconocer.setBorder(BorderFactory.createEmptyBorder());
    contentPane.add(btnReconocer, gbc);

    JLabel lblEstado = new JLabel("Procesando...", SwingConstants.CENTER);
    lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    lblEstado.setOpaque(true);
    lblEstado.setBackground(new Color(116, 116, 116));
    lblEstado.setForeground(Color.WHITE);
    lblEstado.setVisible(false);
    contentPane.add(lblEstado, gbc);

    JLabel lblCompletado = new JLabel("Completado", SwingConstants.CENTER);
    lblCompletado.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    lblCompletado.setOpaque(true);
    lblCompletado.setBackground(new Color(0, 204, 102));
    lblCompletado.setForeground(Color.WHITE);
    lblCompletado.setVisible(false);
    contentPane.add(lblCompletado, gbc);

    JPanel panelBotones = new JPanel(new GridBagLayout());
    panelBotones.setBackground(new Color(245, 245, 245)); // Fondo uniforme
    GridBagConstraints gbcPanelBotones = new GridBagConstraints();
    gbcPanelBotones.fill = GridBagConstraints.BOTH;
    gbcPanelBotones.insets = new Insets(10, 10, 10, 10);
    gbcPanelBotones.weightx = 0.5;
    gbcPanelBotones.gridx = 0;

    JButton btnNombrarCaras = new JButton("Nombrar Caras");
    btnNombrarCaras.setBackground(new Color(255, 102, 0)); // Rojo anaranjado
    btnNombrarCaras.setOpaque(true);
    btnNombrarCaras.setBorder(BorderFactory.createEmptyBorder());
    btnNombrarCaras.setPreferredSize(new Dimension(120, 50)); // Ajustar tamaño según sea necesario
    btnNombrarCaras.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    btnNombrarCaras.setForeground(Color.WHITE);
    btnNombrarCaras.setVisible(false);
    panelBotones.add(btnNombrarCaras, gbcPanelBotones);

    gbcPanelBotones.gridx = 1;

    JButton btnVolverInicio = new JButton("Volver al Inicio");
    btnVolverInicio.setBackground(new Color(192, 192, 192)); // Plateado
    btnVolverInicio.setOpaque(true);
    btnVolverInicio.setBorder(BorderFactory.createEmptyBorder());
    btnVolverInicio.setPreferredSize(new Dimension(120, 50)); // Ajustar tamaño según sea necesario
    btnVolverInicio.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    btnVolverInicio.setForeground(Color.WHITE);
    btnVolverInicio.setVisible(false);
    panelBotones.add(btnVolverInicio, gbcPanelBotones);

    gbc.gridy++;
    contentPane.add(panelBotones, gbc);

    btnReconocer.addActionListener(e -> realizarReconocimiento(imagePanel, btnReconocer, lblEstado, lblCompletado, btnNombrarCaras, btnVolverInicio));

    btnNombrarCaras.addActionListener(e -> mostrarPopupSiHayCarasSinReconocer(imagePanel));
    btnVolverInicio.addActionListener(e -> mostrarPantallaInicio());

    contentPane.revalidate();
    contentPane.repaint();
}

private void realizarReconocimiento(ImagePanel imagePanel, JButton btnReconocer, JLabel lblEstado, JLabel lblCompletado, JButton btnNombrarCaras, JButton btnVolverInicio) {
    btnReconocer.setVisible(false);
    lblEstado.setText("Procesando...");
    lblEstado.setBackground(new Color(116, 116, 116)); // Color inicial gris
    lblEstado.setVisible(true);

    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() throws Exception {
            Timer timer = new Timer(50, e -> updateProcessingColor(lblEstado));
            timer.start();

            try {
                // Simular el reconocimiento real de caras
                JSONObject respuesta = agent.recognize();
                JSONArray caras = respuesta.getJSONArray("caras");
                JSONArray nombres = respuesta.getJSONArray("Nombres");
                JSONArray embeddings = respuesta.getJSONArray("embeddings");
                // System.out.println(respuesta.getString("img_path"));

                for (int i = 0; i < caras.length(); i++) {
                    JSONArray cara = caras.getJSONArray(i);
                    int x = cara.getInt(0);
                    int y = cara.getInt(1);
                    int width = cara.getInt(2);
                    int height = cara.getInt(3);
                    JSONArray embedding = embeddings.getJSONArray(i);
                    String nombre = nombres.getString(i);
                    RecognitionResult result = new RecognitionResult(nombre, x, y, width, height, embedding);
                    imagePanel.recognitionResults.add(result);
                }
            } finally {
                timer.stop();
            }
            return null;
        }

        @Override
        protected void done() {
            imagePanel.repaint(); // Asegúrate de repintar el panel después de actualizar los resultados
            lblEstado.setVisible(false);
            lblCompletado.setVisible(true);

            Timer timer = new Timer(1000, e -> {
                lblCompletado.setVisible(false);
                btnNombrarCaras.setVisible(true);
                btnVolverInicio.setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        }
    };

    worker.execute();
}

private void updateProcessingColor(JLabel lblEstado) {
    if (increasing) {
        brightness+=5;
        if (brightness >= 186) {
            increasing = false;
        }
    } else {
        brightness-=5;
        if (brightness <= 116) {
            increasing = true;
        }
    }
    lblEstado.setBackground(new Color(brightness, brightness, brightness));
}

private void mostrarPopupSiHayCarasSinReconocer(ImagePanel imagePanel) {
    List<BufferedImage> carasSinReconocer = new ArrayList<>();
    List<RecognitionResult> resultadosSinReconocer = new ArrayList<>();
    for (RecognitionResult result : imagePanel.recognitionResults) {
        if (result.getName().isEmpty()) {
            carasSinReconocer.add(imagePanel.getOriginalImage().getSubimage(result.getX(), result.getY(), result.getWidth(), result.getHeight()));
            resultadosSinReconocer.add(result);
        }
    }

    if (!carasSinReconocer.isEmpty()) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel message = new JLabel("Hay " + carasSinReconocer.size() + " caras sin reconocer, ¿deseas nombrar alguna?");
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(message);

        JPanel carasPanel = new JPanel();
        for (BufferedImage cara : carasSinReconocer) {
            JLabel lblCara = new JLabel(new ImageIcon(cara.getScaledInstance(75, 75, Image.SCALE_SMOOTH)));
            carasPanel.add(lblCara);
        }
        panel.add(carasPanel);

        JButton btnSi = new JButton("SI");
        btnSi.setBackground(new Color(0, 204, 102));
        btnSi.setForeground(Color.WHITE);
        btnSi.addActionListener(e -> {
            ((JDialog) SwingUtilities.getWindowAncestor((Component) e.getSource())).dispose();
            mostrarDialogoNombrarCaras(imagePanel, carasSinReconocer, resultadosSinReconocer);
        });

        JButton btnNo = new JButton("NO");
        btnNo.setBackground(new Color(204, 0, 0));
        btnNo.setForeground(Color.WHITE);
        btnNo.addActionListener(e -> {
            ((JDialog) SwingUtilities.getWindowAncestor((Component) e.getSource())).dispose();
        });

        JPanel botonesPanel = new JPanel();
        botonesPanel.add(btnSi);
        botonesPanel.add(btnNo);

        panel.add(botonesPanel);

        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        JDialog dialog = optionPane.createDialog(this, "Caras sin reconocer");
        dialog.setVisible(true);
    }
}

private void mostrarDialogoNombrarCaras(ImagePanel imagePanel, List<BufferedImage> caras, List<RecognitionResult> resultados) {
    if (caras.isEmpty()) {
        return;
    }

    JPanel panel = new JPanel(new BorderLayout());
    JLabel lblCara = new JLabel(new ImageIcon(caras.get(0).getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
    JTextField txtNombre = new JTextField(20);
    JButton btnNombrar = new JButton("Nombrar");
    JButton btnSiguiente = new JButton(">");
    JButton btnAnterior = new JButton("<");

    int[] index = {0};

    btnNombrar.setBackground(new Color(0, 204, 102));
    btnNombrar.setForeground(Color.WHITE);
    List<RecognitionResult> new_results = new ArrayList<>();
    btnNombrar.addActionListener(e -> {
        String nombre = txtNombre.getText();
        if (!nombre.isEmpty()) {
            RecognitionResult currentResult = resultados.get(index[0]);
            currentResult.setName(nombre);

            // Añadimos la imagen a su galería.
            JSONObject nombre_imagen = new JSONObject();
            try {
                JSONArray nombre_array = new JSONArray();
                nombre_array.put(nombre);
                nombre_imagen.put("Nombres", nombre_array);
                nombre_imagen.put("img_path", this.path);
                this.agent.AmpliarGaleria(nombre_imagen);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            

            new_results.add(currentResult);
            JOptionPane.showMessageDialog(this, "Cara nombrada como: " + nombre);
            txtNombre.setText("");

            // 1. Actualizar la imagen principal para mostrar el nombre
            imagePanel.repaint();

            // 2. Eliminar la cara nombrada del diálogo
            caras.remove(index[0]);
            resultados.remove(index[0]);

            // 3. Ajustar el índice y actualizar el contenido del diálogo
            if (index[0] >= caras.size()) {
                index[0]--;
            }
            if (!caras.isEmpty()) {
                lblCara.setIcon(new ImageIcon(caras.get(index[0]).getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
                txtNombre.setText(resultados.get(index[0]).getName());
            } else {
                ((JDialog) SwingUtilities.getWindowAncestor((Component) e.getSource())).dispose();
                // Llamar a actualizarRespuestaGui después de cerrar el diálogo
                // actualizarRespuestaGui(new_results);
            }
        }
    });

    btnSiguiente.addActionListener(e -> {
        if (!txtNombre.getText().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Si no le das a nombrar no se guardará tu resultado. ¿Deseas continuar?", "Advertencia", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.NO_OPTION) {
                return;
            }
        }
        if (index[0] < caras.size() - 1) {
            index[0]++;
            lblCara.setIcon(new ImageIcon(caras.get(index[0]).getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
            txtNombre.setText(resultados.get(index[0]).getName());
        }
    });

    btnAnterior.addActionListener(e -> {
        if (!txtNombre.getText().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Si no le das a nombrar no se guardará tu resultado. ¿Deseas continuar?", "Advertencia", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.NO_OPTION) {
                return;
            }
        }
        if (index[0] > 0) {
            index[0]--;
            lblCara.setIcon(new ImageIcon(caras.get(index[0]).getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
            txtNombre.setText(resultados.get(index[0]).getName());
        }
    });

    JPanel controlPanel = new JPanel();
    controlPanel.add(btnAnterior);
    controlPanel.add(btnNombrar);
    controlPanel.add(btnSiguiente);

    panel.add(lblCara, BorderLayout.CENTER);
    panel.add(txtNombre, BorderLayout.SOUTH);
    panel.add(controlPanel, BorderLayout.NORTH);

    JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
    JDialog dialog = optionPane.createDialog(this, "Nombrar caras");
    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
            // Llamar a actualizarRespuestaGui después de cerrar el diálogo
            actualizarRespuestaGui(new_results);
        }
    });
    dialog.setVisible(true);
}

static class ImagePanel extends JPanel {
    private BufferedImage image;
    public List<RecognitionResult> recognitionResults;

    public ImagePanel(BufferedImage image) {
        this.image = image;
        this.recognitionResults = new ArrayList<>();
        setBackground(new Color(245, 245, 245));
    }

    public void setRecognitionResults(List<RecognitionResult> recognitionResults) {
        this.recognitionResults = recognitionResults;
        repaint(); // Asegura que se repinte cuando se actualizan los resultados
    }

    public BufferedImage getOriginalImage() {
        return image;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            int panelWidth = getWidth();
            int panelHeight = getHeight();

            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth, newHeight;

            if (panelWidth < panelHeight * aspectRatio) {
                newWidth = panelWidth;
                newHeight = (int) (panelWidth / aspectRatio);
            } else {
                newWidth = (int) (panelHeight * aspectRatio);
                newHeight = panelHeight;
            }

            int x = (panelWidth - newWidth) / 2;
            int y = (panelHeight - newHeight) / 2;

            g.drawImage(image, x, y, newWidth, newHeight, this);

            if (recognitionResults != null) {
                for (RecognitionResult result : recognitionResults) {
                    int scaledX = x + (int) (result.getX() * (double) newWidth / originalWidth);
                    int scaledY = y + (int) (result.getY() * (double) newHeight / originalHeight);
                    int scaledWidth = (int) (result.getWidth() * (double) newWidth / originalWidth);
                    int scaledHeight = (int) (result.getHeight() * (double) newHeight / originalHeight);

                    g.setColor(Color.RED);
                    g.drawRect(scaledX, scaledY, scaledWidth, scaledHeight);
                    g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g.drawString(result.getName(), scaledX, scaledY - 10);
                }
            }
        }
    }
}

static class RecognitionResult {
    private String name;
    private int x;
    private int y;
    private int width;
    private int height;
    private JSONArray embeddings;

    public RecognitionResult(String name, int x, int y, int width, int height,JSONArray embeddings) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.embeddings = embeddings;
    }

    public JSONArray getEmbeddings(){
        return embeddings;
    }

    public void setEmbeddings(JSONArray embeddings){
        this.embeddings = embeddings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("x", x);
            jsonObject.put("y", y);
            jsonObject.put("width", width);
            jsonObject.put("height", height);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}

private void actualizarRespuestaGui(List<RecognitionResult> resultados) {
	//System.out.println("Caras Nombradas" + resultados.size() );
    JSONArray carasJsonArray = new JSONArray();
    JSONArray nombresJsonArray = new JSONArray();
    JSONArray embeddingsJsonArray = new JSONArray();

    for (RecognitionResult result : resultados) {
        carasJsonArray.put(result.toJson());
        nombresJsonArray.put(result.getName());
        embeddingsJsonArray.put(result.getEmbeddings());
    }

    JSONObject respuesta = new JSONObject();
    try {
        respuesta.put("caras", carasJsonArray);
        respuesta.put("nombres", nombresJsonArray);
        respuesta.put("embeddings", embeddingsJsonArray);
    } catch (JSONException e) {
        e.printStackTrace();
    }
    this.agent.actualizarNombres(respuesta);
}

    
private void hacerBusqueda() {
    contentPane.removeAll();
    contentPane.revalidate();
    contentPane.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    // gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(20, 20, 10, 20);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 3;
    // gbc.anchor = GridBagConstraints.NORTH;
    gbc.weightx = 1.0;
    gbc.weighty = 0.5;
    crearBotonVolver(contentPane, this::mostrarPantallaInicio);

    JLabel searchLabel = new JLabel("¿A quién desea buscar en la galería?", SwingConstants.CENTER);
    searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
    contentPane.add(searchLabel, gbc);

    gbc.gridy++;
    gbc.gridwidth = 2;
    // gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.CENTER;

    JPanel searchPanel = new JPanel(new BorderLayout());
    searchPanel.setPreferredSize(new Dimension(320, 35));

    JTextField searchField = new JTextField();
    searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    searchField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
    searchField.setPreferredSize(new Dimension(270, 35));

    JButton addButton = new JButton("+");
    addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    addButton.setPreferredSize(new Dimension(50, 35));
    addButton.setBackground(new Color(0, 122, 204));  // Mismo color que el botón buscar
    addButton.setForeground(Color.WHITE);
    addButton.setFocusPainted(false);
    addButton.setBorder(BorderFactory.createEmptyBorder());

    searchPanel.add(searchField, BorderLayout.CENTER);
    searchPanel.add(addButton, BorderLayout.EAST);

    contentPane.add(searchPanel, gbc);

    gbc.gridy++;
    gbc.gridwidth = 3;
    // gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.CENTER;

    JPanel namesPanel = new JPanel();
    namesPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));  // Alineación en flujo, transparente
    namesPanel.setOpaque(false);
    namesPanel.setPreferredSize(new Dimension(360, 100)); // Ajustar tamaño para permitir múltiples entradas
    namesPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    contentPane.add(namesPanel, gbc);

    JPopupMenu suggestionPopup = new JPopupMenu();

    searchField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            showSuggestions();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            showSuggestions();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            showSuggestions();
        }

        private void showSuggestions() {
            suggestionPopup.removeAll();
            String input = searchField.getText();
            if (input.isEmpty()) {
                suggestionPopup.setVisible(false);
                return;
            }
            for (String suggestion : suggestions) {
                if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                    JMenuItem item = new JMenuItem(suggestion);
                    item.addActionListener(e -> {
                        searchField.setText(suggestion);
                        suggestionPopup.setVisible(false);
                        searchField.requestFocusInWindow(); // Ensure the focus returns to the JTextField
                    });
                    suggestionPopup.add(item);
                }
            }
            if (suggestionPopup.getComponentCount() > 0) {
                suggestionPopup.show(searchField, 0, searchField.getHeight());
            } else {
                suggestionPopup.setVisible(false);
            }
            searchField.requestFocusInWindow(); // Ensure the focus stays on the JTextField
        }
    });

    searchField.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            suggestionPopup.setVisible(false); // Hide suggestions when typing
        }
    });

    addButton.addActionListener(e -> {
        String searchText = searchField.getText();
        if (!nombres.contains(searchText) && !searchText.trim().isEmpty()) {
            nombres.add(searchText);
            suggestions.add(searchText);
            addNameLabel(namesPanel, searchText);
            searchField.setText("");
            updateSearchButtonState(); // Actualizar estado del botón de búsqueda
        }
    });

    gbc.gridy++;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;

    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(new Color(245, 245, 245));
    buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // 10 is the gap between buttons

    searchButton = new JButton("Buscar");
    searchButton.setBackground(new Color(192, 192, 192)); // Gris cuando está deshabilitado
    searchButton.setForeground(Color.WHITE);
    searchButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    searchButton.setFocusPainted(false);
    searchButton.setBorder(BorderFactory.createEmptyBorder());
    searchButton.setPreferredSize(new Dimension(220, 65));
    searchButton.setEnabled(false); // Deshabilitar inicialmente

    searchButton.addActionListener(e -> {
        try {
            mostrarFotos();
            nombres.clear();
            namesPanel.removeAll();
            namesPanel.revalidate();
            namesPanel.repaint();
            updateSearchButtonState(); // Actualizar estado del botón de búsqueda
        } catch (JSONException | IOException e1) {
            e1.printStackTrace();
        }
    });

    buttonPanel.add(searchButton);

    contentPane.add(buttonPanel, gbc);

    contentPane.revalidate();
    contentPane.repaint();
}

private void addNameLabel(JPanel namesPanel, String name) {
    JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        }

        @Override
        protected void paintBorder(Graphics g) {
            g.setColor(Color.LIGHT_GRAY);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.width += 0;
            d.height += 0;
            return d;
        }
    };
    namePanel.setOpaque(false);  // Necesario para que se vea el fondo redondeado

    JLabel nameLabel = new JLabel(name);
    nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    nameLabel.setForeground(Color.BLACK);  // Texto en negro

    JButton removeButton = new JButton("x");
    removeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));  // Tamaño de fuente reducido
    removeButton.setPreferredSize(new Dimension(20, 20));
    removeButton.setBackground(Color.WHITE);  // Fondo blanco para la 'x'
    removeButton.setForeground(Color.BLACK);  // 'x' en negro
    removeButton.setFocusPainted(false);
    removeButton.setBorder(BorderFactory.createEmptyBorder());  // Sin borde
    removeButton.addActionListener(e -> {
        namesPanel.remove(namePanel);
        nombres.remove(name);
        namesPanel.revalidate();
        namesPanel.repaint();
        updateSearchButtonState(); // Actualizar estado del botón de búsqueda
    });

    namePanel.add(Box.createHorizontalStrut(0));
    namePanel.add(nameLabel);
    namePanel.add(Box.createHorizontalStrut(20));  // Espacio entre el nombre y la 'x'
    namePanel.add(removeButton);
    namePanel.add(Box.createHorizontalStrut(0));  // Espacio después de la 'x'

    namesPanel.add(namePanel);
    namesPanel.revalidate();
    namesPanel.repaint();
    updateSearchButtonState(); // Actualizar estado del botón de búsqueda
}

private void updateSearchButtonState() {
    if (nombres.isEmpty()) {
        searchButton.setEnabled(false);
        searchButton.setBackground(new Color(192, 192, 192)); // Gris cuando está deshabilitado
    } else {
        searchButton.setEnabled(true);
        searchButton.setBackground(new Color(0, 122, 204)); // Azul cuando está habilitado
    }
}

private void mostrarFotos() throws JSONException, IOException {
    contentPane.removeAll();
    contentPane.revalidate();
    contentPane.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(3, 50, 3, 50);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1.0;
    gbc.weighty = 0.1;
    gbc.anchor = GridBagConstraints.NORTH;
    contentPane.repaint();

    crearBotonVolver(contentPane, this::hacerBusqueda);

    JSONObject paths = agent.mostrarImagenes();
    JSONArray fotos = paths.getJSONArray("fotos");

    if (fotos.length() == 0) {
        JLabel noResultsLabel = new JLabel("No se encontraron resultados", SwingConstants.CENTER);
        noResultsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        contentPane.add(noResultsLabel, gbc);
        contentPane.revalidate();
        contentPane.repaint();
        return;
    }

    JPanel cardPanel = new JPanel(new CardLayout());
    cardPanel.setBackground(new Color(245, 245, 245));
    for (int i = 0; i < fotos.length(); i++) {
        String path = fotos.getString(i);
        BufferedImage originalImage = ImageIO.read(new File(path));
        
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int maxWidth = 400;
        int maxHeight = 400;
        
        int scaledWidth = originalWidth;
        int scaledHeight = originalHeight;
        
        if (originalWidth > maxWidth || originalHeight > maxHeight) {
            double widthRatio = (double) maxWidth / originalWidth;
            double heightRatio = (double) maxHeight / originalHeight;
            double ratio = Math.min(widthRatio, heightRatio);
            
            scaledWidth = (int) (originalWidth * ratio);
            scaledHeight = (int) (originalHeight * ratio);
        }

        Image scaledImage = originalImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        
        JLabel imageLabel = new JLabel(scaledIcon);
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(245, 245, 245));
        imagePanel.add(imageLabel);
        cardPanel.add(imagePanel, String.valueOf(i));
    }
    
    JPanel buttonPanel = new JPanel(new FlowLayout());
    buttonPanel.setBackground(new Color(245, 245, 245));

    JButton prevButton = new JButton("Anterior");
    prevButton.setBackground(new Color(0, 122, 204));
    prevButton.setForeground(Color.WHITE);
    prevButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    prevButton.setFocusPainted(false);
    prevButton.setBorder(BorderFactory.createEmptyBorder());
    prevButton.setPreferredSize(new Dimension(220, 65));

    JButton nextButton = new JButton("Siguiente");
    nextButton.setBackground(new Color(0, 122, 204));
    nextButton.setForeground(Color.WHITE);
    nextButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    nextButton.setFocusPainted(false);
    nextButton.setBorder(BorderFactory.createEmptyBorder());
    nextButton.setPreferredSize(new Dimension(220, 65));

    CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
    prevButton.addActionListener(e -> cardLayout.previous(cardPanel));
    nextButton.addActionListener(e -> cardLayout.next(cardPanel));

    buttonPanel.add(prevButton);
    buttonPanel.add(nextButton);

    gbc.gridx = 0;
    gbc.gridy = 1;
    // gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.anchor = GridBagConstraints.CENTER;
    contentPane.add(cardPanel, gbc);

    gbc.gridy = 2;
    contentPane.add(buttonPanel, gbc);

    contentPane.revalidate();
    contentPane.repaint();
}

private void crearBotonVolver(JPanel panel, Runnable action) {
    JButton btnVolver = new JButton();
    ImageIcon iconBack = new ImageIcon(getClass().getResource("/resources/back_arrow.png"));
    Image imgBack = iconBack.getImage();
    Image newImgBack = imgBack.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
    btnVolver.setIcon(new ImageIcon(newImgBack));
    btnVolver.setBorder(BorderFactory.createEmptyBorder());
    btnVolver.setContentAreaFilled(false);
    btnVolver.addActionListener(e -> action.run());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    panel.add(btnVolver, gbc);
}}

