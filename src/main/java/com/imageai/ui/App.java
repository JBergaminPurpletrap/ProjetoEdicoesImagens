package com.imageai.ui;

import java.io.File;

import javax.imageio.ImageIO;

import com.imageai.service.UpscaleService;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private File selectedFile;
    private Image originalImage;

    private ImageView originalView;
    private ImageView resultView;

    private Slider hueSlider;
    private Slider sharpnessSlider;
    private Slider highlightsSlider;
    private Slider shadowsSlider;
    private Slider whitesSlider;
    private Slider blacksSlider;
    private Slider vignetteIntensitySlider;
    private Slider vignetteMidpointSlider;
    private Slider vignetteRoundnessSlider;

    private Image previewImage;
    private PauseTransition applyPause;

    @Override
    public void start(Stage stage) {

        Label label = new Label("Nenhuma imagem selecionada");

        originalView = new ImageView();
        originalView.setFitWidth(250);
        originalView.setPreserveRatio(true);

        resultView = new ImageView();
        resultView.setFitWidth(250);
        resultView.setPreserveRatio(true);

        Label beforeLabel = new Label("Antes");
        Label afterLabel = new Label("Depois");

        VBox beforeBox = new VBox(5, beforeLabel, originalView);
        beforeBox.setAlignment(Pos.CENTER);

        VBox afterBox = new VBox(5, afterLabel, resultView);
        afterBox.setAlignment(Pos.CENTER);

        HBox imageBox = new HBox(30, beforeBox, afterBox);
        imageBox.setAlignment(Pos.CENTER);

        Button selectButton = new Button("Selecionar Imagem");
        Button upscaleButton = new Button("Upscale");
        Button saveButton = new Button("Salvar Imagem");

        String style = "-fx-background-color: #2d89ef; -fx-text-fill: white; -fx-font-weight: bold;";
        selectButton.setStyle(style);
        upscaleButton.setStyle(style);
        saveButton.setStyle(style);

        selectButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            selectedFile = chooser.showOpenDialog(stage);

            if (selectedFile != null) {
                label.setText("Selecionado: " + selectedFile.getName());
                originalImage = new Image(selectedFile.toURI().toString());
                originalView.setImage(originalImage);
                preparePreviewImage();
                applyImageAdjustments();
            }
        });

        upscaleButton.setOnAction(e -> {
            try {
                if (selectedFile == null) return;

                label.setText("⏳ Melhorando qualidade...");

                File tempFile = new File("output_upscale.png");
                UpscaleService.upscale(
                        selectedFile.getAbsolutePath(),
                        tempFile.getAbsolutePath()
                );

                selectedFile = tempFile;
                originalImage = new Image(tempFile.toURI().toString());
                originalView.setImage(originalImage);
                preparePreviewImage();
                applyImageAdjustments();

                label.setText("✅ Upscale concluído!");
            } catch (Exception ex) {
                ex.printStackTrace();
                label.setText("❌ Erro no upscale");
            }
        });

        saveButton.setOnAction(e -> {
            try {
                if (resultView.getImage() == null) {
                    label.setText("❌ Nada para salvar!");
                    return;
                }

                FileChooser chooser = new FileChooser();
                chooser.setInitialFileName("imagem_editada.png");
                File file = chooser.showSaveDialog(stage);

                if (file != null) {
                    WritableImage finalImage = applyAdjustmentsToImage(originalImage);
                    ImageIO.write(
                            SwingFXUtils.fromFXImage(finalImage, null),
                            "png",
                            file
                    );
                    label.setText("✅ Imagem salva com efeitos!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                label.setText("❌ Erro ao salvar");
            }
        });

        hueSlider = createSlider(-1, 1, 0);
        sharpnessSlider = createSlider(-1, 1, 0);
        highlightsSlider = createSlider(-1, 1, 0);
        shadowsSlider = createSlider(-1, 1, 0);
        whitesSlider = createSlider(-1, 1, 0);
        blacksSlider = createSlider(-1, 1, 0);
        vignetteIntensitySlider = createSlider(0, 1, 0);
        vignetteMidpointSlider = createSlider(0, 1, 0.5);
        vignetteRoundnessSlider = createSlider(0.1, 1, 0.5);

        applyPause = new PauseTransition(Duration.millis(100));
        applyPause.setOnFinished(event -> applyImageAdjustments());

        hueSlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());
        sharpnessSlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());
        highlightsSlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());
        shadowsSlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());
        whitesSlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());
        blacksSlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());
        vignetteIntensitySlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());
        vignetteMidpointSlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());
        vignetteRoundnessSlider.valueProperty().addListener((obs, o, n) -> applyPause.playFromStart());

        VBox column1 = new VBox(8,
                new Label("Matiz"), hueSlider,
                new Label("Nitidez"), sharpnessSlider,
                new Label("Realces"), highlightsSlider,
                new Label("Sombras"), shadowsSlider
        );
        VBox column2 = new VBox(8,
                new Label("Brancos"), whitesSlider,
                new Label("Pretos"), blacksSlider,
                new Label("Vinheta - Intensidade"), vignetteIntensitySlider,
                new Label("Vinheta - Ponto Médio"), vignetteMidpointSlider,
                new Label("Vinheta - Arredondamento"), vignetteRoundnessSlider
        );
        column1.setAlignment(Pos.CENTER_LEFT);
        column2.setAlignment(Pos.CENTER_RIGHT);

        HBox slidersBox = new HBox(30, column1, column2);
        slidersBox.setAlignment(Pos.CENTER);

        VBox editBox = new VBox(30, slidersBox);
        editBox.setAlignment(Pos.CENTER);
        editBox.setMaxWidth(760);

        HBox buttons = new HBox(10, selectButton, upscaleButton, saveButton);
        buttons.setAlignment(Pos.CENTER);

        Label title = new Label("🖼 Image AI Tool");

        VBox root = new VBox(20, title, label, imageBox, buttons, editBox);
        root.setAlignment(Pos.CENTER);
        root.setStyle("""
            -fx-background-color: #1e1e1e;
            -fx-padding: 20;
        """);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-control-inner-background: #1e1e1e;");

        label.setStyle("-fx-text-fill: white;");
        beforeLabel.setStyle("-fx-text-fill: white;");
        afterLabel.setStyle("-fx-text-fill: white;");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Scene scene = new Scene(scrollPane, 780, 750);
        stage.setScene(scene);
        stage.setTitle("Image AI Tool");
        stage.show();
    }

    private Slider createSlider(double min, double max, double value) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4.0);
        slider.setBlockIncrement((max - min) / 100.0);
        return slider;
    }

    private void preparePreviewImage() {
        if (originalImage == null) {
            previewImage = null;
            return;
        }
        previewImage = createPreviewImage(originalImage, 520, 520);
    }

    private void applyImageAdjustments() {
        if (previewImage == null) {
            return;
        }
        resultView.setImage(applyAdjustmentsToImage(previewImage));
    }

    private WritableImage applyAdjustmentsToImage(Image source) {
        if (source == null) {
            return null;
        }

        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        WritableImage edited = new WritableImage(width, height);

        PixelReader reader = source.getPixelReader();
        PixelWriter writer = edited.getPixelWriter();
        Image blurred = createBlurredImage(source);
        PixelReader blurReader = blurred.getPixelReader();

        double hueShift = hueSlider.getValue() * 180;
        double sharpnessAmount = sharpnessSlider.getValue();
        double highlightsAmount = highlightsSlider.getValue();
        double shadowsAmount = shadowsSlider.getValue();
        double whitesAmount = whitesSlider.getValue();
        double blacksAmount = blacksSlider.getValue();
        double vignetteIntensity = vignetteIntensitySlider.getValue();
        double vignetteMidpoint = vignetteMidpointSlider.getValue();
        double vignetteRoundness = Math.max(0.1, vignetteRoundnessSlider.getValue());

        double centerX = width / 2.0;
        double centerY = height / 2.0;
        double radius = 1.0 - vignetteMidpoint * 0.9;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);
                int alpha = (argb >> 24) & 0xff;
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;

                javafx.scene.paint.Color color = javafx.scene.paint.Color.rgb(r, g, b);

                double hue = (color.getHue() + hueShift) % 360;
                if (hue < 0) {
                    hue += 360;
                }
                double saturation = color.getSaturation();
                double brightness = color.getBrightness();

                if (shadowsAmount != 0 && brightness < 0.5) {
                    brightness += shadowsAmount * (0.5 - brightness) * 0.6;
                }
                if (highlightsAmount != 0 && brightness > 0.5) {
                    brightness += highlightsAmount * (brightness - 0.5) * 0.6;
                }
                if (blacksAmount != 0 && brightness < 0.3) {
                    brightness += blacksAmount * (0.3 - brightness) * 0.7;
                }
                if (whitesAmount != 0 && brightness > 0.7) {
                    brightness += whitesAmount * (brightness - 0.7) * 0.7;
                }
                brightness = clamp(brightness, 0, 1);

                javafx.scene.paint.Color adjusted = javafx.scene.paint.Color.hsb(hue, saturation, brightness);

                javafx.scene.paint.Color blurColor = javafx.scene.paint.Color.rgb(
                        (int) Math.round(((blurReader.getArgb(x, y) >> 16) & 0xff)),
                        (int) Math.round(((blurReader.getArgb(x, y) >> 8) & 0xff)),
                        (int) Math.round((blurReader.getArgb(x, y) & 0xff))
                );

                double red = adjusted.getRed();
                double green = adjusted.getGreen();
                double blue = adjusted.getBlue();

                double blurRed = blurColor.getRed();
                double blurGreen = blurColor.getGreen();
                double blurBlue = blurColor.getBlue();

                red = red + sharpnessAmount * (red - blurRed) * 0.7;
                green = green + sharpnessAmount * (green - blurGreen) * 0.7;
                blue = blue + sharpnessAmount * (blue - blurBlue) * 0.7;

                double nx = (x - centerX) / centerX;
                double ny = (y - centerY) / centerY;
                double distance = Math.sqrt(nx * nx + (ny * ny) / (vignetteRoundness * vignetteRoundness));
                double vignetteFactor = clamp((distance - radius) / (1.0 - radius), 0, 1) * vignetteIntensity;
                double darken = vignetteFactor * 0.75;

                red = clamp(red * (1 - darken), 0, 1);
                green = clamp(green * (1 - darken), 0, 1);
                blue = clamp(blue * (1 - darken), 0, 1);

                int finalArgb = (alpha << 24)
                        | ((int) Math.round(red * 255) << 16)
                        | ((int) Math.round(green * 255) << 8)
                        | (int) Math.round(blue * 255);

                writer.setArgb(x, y, finalArgb);
            }
        }

        return edited;
    }

    private Image createPreviewImage(Image source, double maxWidth, double maxHeight) {
        if (source == null || source.getUrl() == null) {
            return source;
        }
        return new Image(source.getUrl(), maxWidth, maxHeight, true, true);
    }

    private Image createBlurredImage(Image source) {
        int width = (int) source.getWidth();
        int height = (int) source.getHeight();
        WritableImage blurred = new WritableImage(width, height);
        PixelReader reader = source.getPixelReader();
        PixelWriter writer = blurred.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int count = 0;
                double totalR = 0;
                double totalG = 0;
                double totalB = 0;

                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            int neighbor = reader.getArgb(nx, ny);
                            totalR += (neighbor >> 16) & 0xff;
                            totalG += (neighbor >> 8) & 0xff;
                            totalB += neighbor & 0xff;
                            count++;
                        }
                    }
                }

                int avgR = clamp((int) Math.round(totalR / count), 0, 255);
                int avgG = clamp((int) Math.round(totalG / count), 0, 255);
                int avgB = clamp((int) Math.round(totalB / count), 0, 255);
                int alpha = (reader.getArgb(x, y) >> 24) & 0xff;
                int blurredArgb = (alpha << 24) | (avgR << 16) | (avgG << 8) | avgB;
                writer.setArgb(x, y, blurredArgb);
            }
        }

        return blurred;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static void main(String[] args) {
        launch();
    }
}
