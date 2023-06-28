# Antora site documentation

## Requirements

You’ve installed an active Node LTS release on your Linux, Windows, or macOS machine.

## Install globally antora

Execute in your console:

`npm i -g @antora/cli@3.0 @antora/site-generator@3.0`

You can verify that the `antora` command is available on your path by running:

`antora -v`

## Compile the doc site

Execute in your console:

`npx antora --fetch antora-playbook.yml`

## Edit with Visual Studio Code

The docs folder contains a devcontainers configuration for editing the documentation with Visual Studio Code. It automatically creates a Docker-based environment with all the required dependencies (included Node and Antora) provisioning the Code extensions required for editing the Asciidoc files.

It also defines to tasks (executables from the command palette with the command `Tasks: Run Task`):

- `Antora: Build Site:` executes the Antora build script and generates the site.
- `Antora: Start Server:` starts an HTTP server and automatically opens a browser tab previewing the site. The server starts with catching disabled so after building the site the changes are inmediately available.
