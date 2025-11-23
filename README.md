# Sistema de Gerenciamento de Biblioteca 📚

Sistema de gerenciamento de biblioteca desenvolvido em Java com interface de terminal, implementando algoritmos customizados de ordenação e busca.

## 📋 Descrição

Este projeto é um sistema de gerenciamento de biblioteca que permite realizar operações básicas (CRUD) em uma coleção de livros. Todos os dados são armazenados em memória e a interface é totalmente em português brasileiro.

## ✨ Funcionalidades

- **➕ Adicionar Livro**: Adiciona novos livros à biblioteca com título e autor
- **🗑️ Remover Livro**: Remove livros usando o ISBN
- **✏️ Atualizar Livro**: Atualiza informações de livros existentes
- **🔍 Buscar Livro**: Busca rápida por título usando busca binária
- **📖 Listar Todos**: Lista todos os livros ordenados alfabeticamente
- **📊 Estatísticas**: Mostra informações sobre a biblioteca

## 🎯 Características Técnicas

### Algoritmos Implementados (Sem uso de bibliotecas Java)

1. **Merge Sort** - Algoritmo de ordenação O(n log n)
   - Implementado manualmente em `src/algorithm/MergeSort.java`
   - Usado para manter os livros ordenados por título
   - Não utiliza `Arrays.sort()` ou `Collections.sort()`

2. **Binary Search** - Algoritmo de busca O(log n)
   - Implementado manualmente em `src/algorithm/BinarySearch.java`
   - Usado para buscar livros por título de forma eficiente
   - Não utiliza `Arrays.binarySearch()`

### Base de Dados Inicial

- Carrega livros iniciais de um arquivo CSV local (`base-books.csv`)
- Contém ~600 livros de autores famosos
- ISBN único para cada livro (gerado com UUID)
- Dados pré-populados para uso imediato

## 📁 Estrutura do Projeto

```
library-management-system/
├── src/
│   ├── Main.java                    # Ponto de entrada
│   ├── model/
│   │   └── Book.java               # Modelo de dados do livro
│   ├── service/
│   │   ├── LibraryService.java     # Lógica de negócio (CRUD)
│   │   └── BookApiService.java     # Carregador de CSV
│   ├── algorithm/
│   │   ├── MergeSort.java          # Algoritmo de ordenação
│   │   └── BinarySearch.java       # Algoritmo de busca
│   └── ui/
│       └── ConsoleUI.java          # Interface do usuário
├── bin/                             # Classes compiladas
├── base-books.csv                   # Base de dados inicial
├── run.sh                           # Script de execução
└── README.md                        # Documentação
```

## 🚀 Como Executar

### Opção 1: Usando o script (Linux/Mac)

```bash
./run.sh
```

### Opção 2: Manualmente

```bash
# Compilar
mkdir -p bin
javac -d bin src/**/*.java src/*.java

# Executar
java -cp bin Main
```

### Requisitos

- Java JDK 8 ou superior

## 💡 Como Usar

1. **Ao iniciar**: O sistema carrega ~600 livros do arquivo CSV local
2. **Menu Principal**: Escolha uma opção digitando o número (1-7)
3. **ISBN**: Ao adicionar um livro, um ISBN será gerado - salve-o para operações futuras
4. **Busca**: A busca é feita por título (pode ser parcial)
5. **Ordenação**: Os livros são mantidos sempre ordenados alfabeticamente

## 📚 Exemplo de Uso

```
MENU PRINCIPAL
==================================================
  1. 📚 Adicionar um livro
  2. 🗑️  Remover um livro
  3. ✏️  Atualizar um livro
  4. 🔍 Buscar livro por título
  5. 📖 Listar todos os livros
  6. 📊 Ver estatísticas
  7. 🚪 Sair
==================================================
Escolha uma opção: 1

Digite o título do livro: Dom Casmurro
Digite o autor do livro: Machado de Assis

✅ Livro adicionado com sucesso!
📌 ISBN gerado: 1234567890123
💡 Salve este ISBN para futuras operações de atualização ou remoção.
```

## 🔧 Detalhes de Implementação

### Classe Book
- Atributos: `titulo`, `autor`, `isbn`
- `equals()` e `hashCode()` baseados no ISBN
- `toString()` formatado para exibição

### LibraryService
- Gerencia ArrayList de livros em memória
- Reordena a lista após cada modificação
- Validação de entrada e prevenção de duplicatas

### MergeSort
- Implementação recursiva clássica
- Compara títulos (case-insensitive)
- Complexidade: O(n log n)

### BinarySearch
- Busca em lista ordenada
- Suporta busca parcial (substring)
- Retorna todas as correspondências
- Complexidade: O(log n)

## 🎓 Objetivo Acadêmico

Este projeto foi desenvolvido para a disciplina de **Estruturas de Dados e Análise de Algoritmos**, com foco em:
- Implementação manual de algoritmos fundamentais
- Análise de complexidade (Big O)
- Boas práticas de programação Java
- Arquitetura em camadas (Model-Service-UI)

## 📝 Autores Incluídos na Base Inicial

A base de dados `base-books.csv` contém livros de diversos autores famosos:

- Clarice Lispector
- Freida McFadden
- Gergely Orosz
- Stephen King
- Agatha Christie
- Ernest Hemingway
- Mark Twain
- Jane Austen
- William Shakespeare
- Leo Tolstoy
- Anton Chekhov
- Virginia Woolf
- J.K. Rowling

## 📦 Formato do Arquivo CSV

O arquivo `base-books.csv` contém as seguintes colunas:
- **titulo**: Título do livro
- **autor**: Nome do autor
- **isbn**: ISBN único de 13 dígitos
- **ano**: Ano de publicação (opcional)

Os dados foram obtidos da **Open Library API** (https://openlibrary.org/developers/api)
