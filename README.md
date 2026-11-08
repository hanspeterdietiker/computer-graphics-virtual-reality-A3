# Sistema Solar 3D em OpenGL 🌌

Trabalho A3 - Computação Gráfica e Realidade Virtual

Instituição: UNIFACS

Professor: Enderson Nobre Santos

Data de Entrega: 03/12/2026

### 📝 Descrição do Projeto

Este projeto consiste em uma aplicação gráfica desenvolvida em Java utilizando a API OpenGL. 

O objetivo é renderizar um ambiente tridimensional que simula o Sistema Solar, demonstrando na prática os conceitos de modelagem computacional e computação gráfica.

A simulação apresenta os corpos celestes em ambiente 3D, aplicando matrizes de transformação geométrica para representar as órbitas e as rotações dos planetas em torno de seus próprios eixos e do Sol.

### 👥 Integrantes do Grupo

Hanspeter Dietiker (RA: 1272313332)

Edioelson Júnior A. B. Teixeira (RA: 1272318423)


### ⚙️ Arquitetura do Código

O projeto foi estruturado de forma modular para separar a lógica da interface, renderização gráfica e física dos objetos:

Main / Janela: Ponto de entrada da aplicação. Gerencia a criação da interface do usuário e inicializa o contexto OpenGL (ex: JOGL).

Renderizador: Contém o loop principal (display) responsável por redesenhar a cena e aplicar iluminação e texturas.

CorpoCeleste: Classe que abstrai os planetas e o Sol, armazenando características como raio, distância orbital, velocidade de translação, velocidade de rotação e cor/textura.

Câmera: Gerencia as matrizes de projeção e visualização, permitindo a navegação pelo espaço 3D (Pan, Zoom, Rotação).

### 🧮 Modelos Matemáticos

O projeto faz uso intenso de modelos matemáticos da computação gráfica, incluindo:

Matrizes de Translação e Rotação: Essenciais para calcular a posição de cada planeta em sua órbita com base no tempo contínuo.

Projeção Perspectiva: Para garantir a profundidade realista do cenário espacial.

Primitivas Geométricas: Algoritmos de geração de malhas esféricas para a renderização volumétrica dos planetas.

### 🚀 Como Executar

#### Pré-requisitos

- **JDK 17** (ou superior), com a variável `JAVA_HOME` apontando para ele.
- **Maven não é necessário:** o projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`), que baixa o Maven 3.9.9 na primeira execução.
- O JOGL 2.5.0 e os nativos de Windows, Linux e macOS são baixados automaticamente pelo Maven (repositório da JogAmp).

#### Compilar, testar e executar

```bash
# Windows (PowerShell / cmd)
.\mvnw.cmd clean package     # compila, roda os testes e gera o JAR em target/
.\mvnw.cmd test              # roda apenas os testes (JUnit 5)
.\mvnw.cmd compile exec:java # executa a classe Main

# Linux / macOS / Git Bash
./mvnw clean package
```

#### Abrir na IDE

- **IntelliJ IDEA:** *File → Open* e selecione a pasta do projeto (ou o `pom.xml`). O IntelliJ importa o projeto Maven automaticamente.
- **Eclipse:** *File → Import → Maven → Existing Maven Projects* e selecione a pasta do projeto.

Execute a classe `br.unifacs.a3.sistemasolar.Main` adicionando nas **VM options** da configuração de execução:

```
--add-opens java.desktop/sun.awt=ALL-UNNAMED
```

O JOGL acessa classes internas do AWT por reflexão, e o Java 17 bloqueia esse acesso por padrão. Pelo `mvnw exec:java` a flag já é aplicada automaticamente (arquivo `.mvn/jvm.config`).

### 📊 Avaliação

Projeto referente à avaliação A3, contemplando:

[x] Código-fonte em Java: 10 pontos.

[x] Relatório Técnico: 10 pontos.

[ ] Apresentação: 10 pontos.