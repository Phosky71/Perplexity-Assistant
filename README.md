# Perplexity Assistant

> ⚠️ **This project is currently under active development (Work In Progress)**. Features may be incomplete, unstable, or subject to change. Use at your own risk and feel free to contribute!

An IntelliJ IDEA plugin that integrates Perplexity AI directly into your IDE, providing intelligent code assistance and chat functionality.

## Features

- **AI Chat Panel**: Interactive chat interface within the IDE
- **Code Context Awareness**: Send selected code to Perplexity for analysis
- **Insert/Replace Code**: Apply AI suggestions directly to your editor
- **Persistent API Key**: Save your Perplexity API key securely in settings
- **Conversation History**: Maintain chat context across sessions

## Installation

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

## Documentation

See the [`docs/`](docs/) directory for architecture, development, security, operations, and project state. Architecture decisions are recorded in [`docs/decisions/`](docs/decisions/).

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b main`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin main`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Perplexity AI](https://www.perplexity.ai/) for their powerful API
- [JetBrains](https://www.jetbrains.com/) for the IntelliJ Platform SDK

## Support

If you encounter any issues or have suggestions, please [open an issue](https://github.com/Phosky71/Perplexity-Assistant/issues).

## Project Status

Current version: **1.3.0**. The plugin is under active development and has not been published to the JetBrains Plugin Marketplace yet (plugin name and publication are pending, see ADR-008 and TECH-003).

Implemented so far:
- Chat tool window with Markdown rendering and code actions (copy/insert/replace).
- Send selection / send file context with preview.
- Quick actions: Explain, Review, Refactor, Tests, Docs.
- Model selector (sonar, sonar-pro, sonar-reasoning-pro) with per-model cost estimation.
- Usage tracking with monthly limit and 80% warning.
- Optional local chat history (opt-in).
- Request cancellation.
- Secure API key storage via PasswordSafe.

Compatibility verified with IntelliJ Platform 231, 241 and 252 (erifyPlugin).

## Future Improvements

Potential next steps (see docs/product/PROPOSALS-2026-10-23.md):
- Streaming responses (if the API supports it).
- Kotlin UI DSL migration (UI-002, planned for 1.4.0).
- Persisted multi-conversation history and export.

Contributions from the community are welcome.