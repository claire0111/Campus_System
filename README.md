## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).

檔案架構
Campus_System
├─.vscode
│  ├──launch.json
│  └──settings.json
├──java_frontFX
│  ├──JAVAFX
│  │  ├──bin
│  │  ├──data
│  │  │  ├──activity sheet.csv
│  │  │  ├──registration list.csv
│  │  │  ├──student list.csv
│  │  │  └──teacher list.csv
│  │  └──src
│  │  │  ├──images
│  │  │  ├──controller
│  │  │  │  ├──AdminController.java
│  │  │  │  ├──EventController.java
│  │  │  │  └──LoginController.java
│  │  │  ├──model
│  │  │  │  ├──Event.java
│  │  │  │  ├──Registration.java
│  │  │  │  └──User.java
│  │  │  ├──service
│  │  │  │  ├──EventService.java
│  │  │  │  ├──FileService.java
│  │  │  │  ├──LoginService.java
│  │  │  │  └──RegistrationService.java
│  │  │  ├──util
│  │  │  │  ├──CsvUtil.java
│  │  │  │  └──EventStatusUtil.java
│  │  │  ├──view
│  │  │  │  ├──EventDialogView.java
│  │  │  │  ├──EventTableView.java
│  │  │  │  ├──EventView.java
│  │  │  │  ├──LoginView.java
│  │  │  │  ├──MainView.java
│  │  │  │  ├──NavbarView.java
│  │  │  │  ├──RegisteredTableView.java
│  │  │  │  ├──RegistrationView.java
│  │  │  │  └──SearchView.java
│  │  │  ├──Launcher.java
│  │  │  ├──Main.java
│  │  │  └──style.css
│  └──lib
└──javafx-sdk-24.0.1
