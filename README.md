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

Pré-requisitos

Java Development Kit (JDK) instalado (versão X ou superior).

Bibliotecas da API OpenGL para Java (como JOGL ou LWJGL) configuradas no classpath do projeto.

Passos para compilação

Clone este repositório:

git clone https://github.com/seu-usuario/sistema-solar-opengl.git


Abra o projeto na sua IDE de preferência (Eclipse, IntelliJ IDEA, etc.).

Certifique-se de que as bibliotecas do OpenGL estão importadas corretamente nas dependências do projeto.

Execute o arquivo da classe principal (Main.java).

### 📊 Avaliação

Projeto referente à avaliação A3, contemplando:

[x] Código-fonte em Java: 10 pontos.

[x] Relatório Técnico: 10 pontos.

[ ] Apresentação: 10 pontos.