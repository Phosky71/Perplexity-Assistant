# Perplexity Assistant

An IntelliJ IDEA plugin that integrates Perplexity AI directly into your IDE, providing intelligent code assistance and chat functionality.

## Features

- **AI Chat Panel**: Interactive chat interface within the IDE
- **Code Context Awareness**: Send selected code to Perplexity for analysis
- **Insert/Replace Code**: Apply AI suggestions directly to your editor
- **Persistent API Key**: Save your Perplexity API key securely in settings
- **Conversation History**: Maintain chat context across sessions

## Installation

### From JetBrains Marketplace
1. Open your JetBrains IDE (IntelliJ IDEA, PyCharm, WebStorm, etc.)
2. Go to `Settings/Preferences` > `Plugins` > `Marketplace`
3. Search for "Perplexity Assistant"
4. Click `Install`

### Manual Installation
1. Download the latest release from [Releases](https://github.com/Phosky71/Perplexity-Assistant/releases)
2. Go to `Settings/Preferences` > `Plugins` > `⚙️` > `Install Plugin from Disk...`
3. Select the downloaded `.zip` file

## Configuration

1. Go to `Settings/Preferences` > `Tools` > `Perplexity Assistant`
2. Enter your Perplexity API key
3. Click `Apply`

You can get your API key from [Perplexity API Settings](https://www.perplexity.ai/settings/api).

## Usage

### Opening the Chat Panel
- Click on the **Perplexity** tool window on the right side of your IDE
- Or use `View` > `Tool Windows` > `Perplexity Assistant`

### Sending Code Context
1. Select code in your editor
2. Right-click and select `Send to Perplexity`
3. Or use the keyboard shortcut (configurable in settings)

### Applying Suggestions
- **Insert**: Adds AI-generated code at cursor position
- **Replace**: Replaces selected code with AI suggestion

## Requirements

- IntelliJ IDEA 2023.1+ (or compatible JetBrains IDE)
- Java 17+
- Perplexity API key

## Building from Source

```bash
# Clone the repository
git clone https://github.com/Phosky71/Perplexity-Assistant.git
cd Perplexity-Assistant

# Build the plugin
./gradlew buildPlugin

# The plugin zip will be in build/distributions/
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Perplexity AI](https://www.perplexity.ai/) for their powerful API
- [JetBrains](https://www.jetbrains.com/) for the IntelliJ Platform SDK

## Support

If you encounter any issues or have suggestions, please [open an issue](https://github.com/Phosky71/Perplexity-Assistant/issues).
