#!/bin/bash

# Script para compilar e executar o Sistema de Gerenciamento de Biblioteca

echo "Compilando o projeto com Maven..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo ""
    echo "Compilação bem-sucedida!"
    echo ""
    echo "Executando o aplicativo..."
    echo ""
    mvn exec:java -Dexec.mainClass="Main" -q
else
    echo "Erro na compilação!"
    exit 1
fi
