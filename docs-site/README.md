# Antora site documentation

## Requirements

You’ve installed an active Node LTS release on your Linux, Windows, or macOS machine.

## Install globally antora

Execute in your console:

`npm i -g @antora/cli@3.0 @antora/site-generator@3.0`

You can verify that the `antora` command is available on your path by running:

` antora -v`

## Compile the doc site

Execute in your console:

`npx antora --fetch antora-playbook.yml`
