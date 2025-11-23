#!/bin/bash

# Script para compilar e executar o Sistema de Gerenciamento de Biblioteca

echo "Compilando o projeto..."

# Cria diretório bin se não existir
mkdir -p bin

# Compila todos os arquivos Java
javac -d bin src/**/*.java src/*.java

if [ $? -eq 0 ]; then
    echo ""
    echo "Compilação bem-sucedida!"
    echo ""
    echo "Executando o aplicativo..."
    echo ""
    java -cp bin Main
else
    echo "Erro na compilação!"
    exit 1
fi
