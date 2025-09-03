# Contributing to RS3 Cooking Script

Thank you for your interest in contributing to the RS3 Cooking Script! We welcome contributions from the community and appreciate your help in making this project better.

## 🤝 How to Contribute

### Reporting Issues
- **Search existing issues** first to avoid duplicates
- **Use the issue templates** when available
- **Provide detailed information** including:
  - BotWithUs version
  - Operating system
  - Steps to reproduce
  - Expected vs actual behavior
  - Error messages or logs

### Suggesting Features
- **Check existing feature requests** to avoid duplicates
- **Describe the feature** clearly and thoroughly
- **Explain the use case** and why it would be beneficial
- **Consider implementation complexity** and maintenance impact

### Code Contributions
1. **Fork the repository** on GitHub
2. **Create a feature branch** from `main`
3. **Make your changes** following our coding standards
4. **Test thoroughly** to ensure functionality
5. **Submit a pull request** with a clear description

## 🛠️ Development Setup

### Prerequisites
- **Java 17+** - Required for compilation
- **IntelliJ IDEA** - Recommended IDE
- **Git** - For version control
- **BotWithUs Client** - For testing

### Local Development
```bash
# Clone your fork
git clone https://github.com/your-username/rs3-cook.git
cd rs3-cook

# Create a feature branch
git checkout -b feature/your-feature-name

# Build the project
gradlew.bat build  # Windows
./gradlew build    # Linux/Mac

# Test your changes
# (Manual testing with BotWithUs client)
```

### Project Structure
```
src/main/java/net/botwithus/rs3cook/
├── CookingScript.java           # Main script class
├── config/                      # Configuration system
├── data/                        # Fish and location data
├── cooking/                     # Cooking logic
├── banking/                     # Banking system
├── safety/                      # Safety and anti-detection
├── statistics/                  # Statistics and logging
└── ui/                         # User interface
```

## 📝 Coding Standards

### Java Code Style
- **Use Java 17+ features** appropriately
- **Follow standard naming conventions**:
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
- **Add JavaDoc** for public methods and classes
- **Keep methods focused** and reasonably sized
- **Use meaningful variable names**

### Code Organization
- **Separate concerns** into appropriate packages
- **Use dependency injection** where appropriate
- **Avoid static state** when possible
- **Handle exceptions** gracefully
- **Log important events** for debugging

### Documentation
- **Comment complex logic** and algorithms
- **Update README** if adding new features
- **Include usage examples** for new functionality
- **Document configuration options**

## 🧪 Testing Guidelines

### Manual Testing
- **Test with different fish types** and locations
- **Verify safety features** work correctly
- **Check UI responsiveness** and functionality
- **Test configuration persistence**
- **Validate statistics accuracy**

### Test Scenarios
- **Fresh installation** - First-time user experience
- **Configuration changes** - Settings persistence
- **Error conditions** - Network issues, game updates
- **Long sessions** - Memory usage and stability
- **Safety triggers** - Emergency stops and breaks

### Performance Testing
- **Memory usage** - Monitor for memory leaks
- **CPU usage** - Ensure reasonable resource consumption
- **Response times** - UI should remain responsive
- **Statistics accuracy** - Verify calculations

## 📋 Pull Request Process

### Before Submitting
1. **Ensure your code compiles** without warnings
2. **Test thoroughly** with the BotWithUs client
3. **Update documentation** if necessary
4. **Follow the coding standards** outlined above
5. **Rebase your branch** on the latest main

### Pull Request Description
Include the following information:
- **Summary** of changes made
- **Motivation** for the changes
- **Testing performed** and results
- **Breaking changes** if any
- **Related issues** (use "Fixes #123" syntax)

### Review Process
- **Maintainers will review** your pull request
- **Address feedback** promptly and professionally
- **Make requested changes** in additional commits
- **Squash commits** before merging if requested

## 🎯 Areas for Contribution

### High Priority
- **Banking API improvements** - When BotWithUs APIs become available
- **Additional fish types** - New RS3 content support
- **Performance optimizations** - Memory and CPU efficiency
- **Bug fixes** - Address reported issues

### Medium Priority
- **UI enhancements** - Improved user experience
- **Additional statistics** - More detailed tracking
- **Configuration options** - More customization
- **Documentation improvements** - Clearer guides

### Low Priority
- **Code refactoring** - Improve maintainability
- **Additional safety features** - Enhanced anti-detection
- **Export features** - Data export capabilities
- **Localization** - Multi-language support

## 🚫 What Not to Contribute

### Avoid These Changes
- **Malicious code** or backdoors
- **Copyright violations** or stolen code
- **Features that violate** game terms of service
- **Overly complex solutions** to simple problems
- **Breaking changes** without discussion

### Code Quality Issues
- **Poorly documented** or uncommented code
- **Hardcoded values** that should be configurable
- **Memory leaks** or resource management issues
- **Security vulnerabilities** or unsafe practices

## 📞 Getting Help

### Communication Channels
- **GitHub Issues** - For bugs and feature requests
- **GitHub Discussions** - For general questions
- **BotWithUs Forums** - For community support
- **Code Reviews** - For implementation feedback

### Before Asking for Help
1. **Search existing issues** and documentation
2. **Try to reproduce** the issue consistently
3. **Gather relevant information** (versions, logs, etc.)
4. **Provide clear examples** of the problem

## 🏆 Recognition

### Contributors
All contributors will be:
- **Listed in the README** (if desired)
- **Credited in release notes** for significant contributions
- **Thanked publicly** for their efforts

### Types of Contributions
We value all types of contributions:
- **Code contributions** - New features and bug fixes
- **Documentation** - Guides, examples, and improvements
- **Testing** - Bug reports and feature validation
- **Community support** - Helping other users
- **Feedback** - Suggestions and usability improvements

## 📄 License

By contributing to this project, you agree that your contributions will be licensed under the same MIT License that covers the project. See the [LICENSE](LICENSE) file for details.

---

**Thank you for contributing to the RS3 Cooking Script!** Your efforts help make this project better for the entire BotWithUs community. 🎉
