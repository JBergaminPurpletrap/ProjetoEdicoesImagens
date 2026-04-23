# ProjetoEdicoesImagens

Editor de imagens JavaFX com recursos de upscale e ajustes visuais.

## 📋 Descrição

Aplicação desktop para edição de imagens com interface gráfica JavaFX. Permite selecionar imagens, aplicar ajustes visuais e realizar upscale (aumento de resolução).

## 🚀 Funcionalidades

- **Seleção de Imagem**: Escolher imagens do computador
- **Upscale**: Melhorar qualidade e resolução da imagem (2x)
- **Ajustes de Imagem**:
  - Matiz (Hue)
  - Nitidez (Sharpness)
  - Realces (Highlights)
  - Sombras (Shadows)
  - Brancos (Whites)
  - Pretos (Blacks)
  - Vinheta (Intensidade, Ponto médio, Arredondamento)
- **Comparação**: Visualização lado a lado (Antes/Depois)
- **Salvar**: Exportar imagem editada

## 🛠️ Tecnologias

- **Java** 25
- **JavaFX** 21
- **Maven**

## 📦 Como Executar

```bash
mvn clean compile
mvn javafx:run
```

Ou compile e execute via IDE (IntelliJ, Eclipse, VS Code).

## 📁 Estrutura do Projeto

```
src/main/java/com/imageai/
├── Main.java              # Ponto de entrada
├── ui/
│   └── App.java           # Interface JavaFX
└── service/
    └── UpscaleService.java # Serviço de upscale
```

## 🎨 Interface

A interface apresenta:
- Área de visualização antes/depois
- Controles deslizantes para ajustes
- Botões para selecionar, fazer upscale e salvar

## 📝 Licença

Este projeto está para fins educativos.