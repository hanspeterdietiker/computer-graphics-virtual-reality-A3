<div align="center">

# 🌌 Sistema Solar 3D em OpenGL

**Trabalho A3 — Computação Gráfica e Realidade Virtual**

[![Java CI with Maven](https://github.com/hanspeterdietiker/computer-graphics-virtual-reality-A3/actions/workflows/ci.yml/badge.svg)](https://github.com/hanspeterdietiker/computer-graphics-virtual-reality-A3/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)
![JOGL](https://img.shields.io/badge/JOGL-2.5.0-5586A4)
![OpenGL](https://img.shields.io/badge/OpenGL-GL2-5586A4?logo=opengl&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

| | |
|---|---|
| 🏫 **Instituição** | UNIFACS |
| 👨‍🏫 **Professor** | Enderson Nobre Santos |
| 📅 **Entrega** | 03/12/2026 |

---

## 📑 Sumário

- [Descrição do Projeto](#-descrição-do-projeto)
- [Integrantes do Grupo](#-integrantes-do-grupo)
- [Tecnologias](#️-tecnologias)
- [Arquitetura do Código](#️-arquitetura-do-código)
- [Modelos Matemáticos](#-modelos-matemáticos)
- [Como Executar](#-como-executar)
- [Controles](#-controles)
- [Documentação](#-documentação)
- [Avaliação](#-avaliação)
- [Licença](#-licença)

---

## 📝 Descrição do Projeto

Aplicação gráfica desktop desenvolvida em **Java** com a API **OpenGL** (via JOGL) que renderiza um ambiente tridimensional simulando o **Sistema Solar**, demonstrando na prática os conceitos de modelagem computacional e computação gráfica.

A simulação apresenta o Sol, os 8 planetas e a Lua em um ambiente 3D, aplicando **matrizes de transformação geométrica** para representar as órbitas (translação) e as rotações dos corpos em torno de seus próprios eixos e do Sol.

**Principais características:**

- 🪐 Hierarquia de transformações `Sol → Planeta → Lua`
- ⏱️ Animação a 60 FPS com *delta time*, independente da taxa de quadros
- 🎥 Câmera orbital com rotação, pan e zoom
- 💡 Iluminação a partir do Sol e texturas nos corpos celestes
- 🧪 Matemática (vetores, matrizes e malha esférica) coberta por testes unitários

---

## 👥 Integrantes do Grupo

| Nome | RA |
|---|---|
| Hanspeter Dietiker | 1272313332 |
| Edioelson Júnior A. B. Teixeira | 1272318423 |

---

## 🛠️ Tecnologias

| Item | Escolha | Motivo |
|---|---|---|
| Linguagem | **Java 17 (LTS)** | Exigência do trabalho; versão estável com o JOGL |
| Binding OpenGL | **JOGL 2.5.0** | Integração com Swing via `GLCanvas` |
| Perfil OpenGL | **GL2** (pipeline fixo + VBO) | Permite mostrar a hierarquia de transformações de forma didática com `glPushMatrix`/`glPopMatrix` |
| Build | **Maven** (com Maven Wrapper) | Resolve dependências e nativos do JOGL; abre em IntelliJ e Eclipse |
| Testes | **JUnit 5** | Testa a matemática sem precisar de contexto gráfico |
| CI | **GitHub Actions** | Compila e roda os testes a cada *push* e *pull request* na `main` |

---

## 🏗️ Arquitetura do Código

O projeto é modular, separando janela, renderização, simulação, câmera e matemática:

```
src/main/java/br/unifacs/a3/sistemasolar/
├── Main.java        # ponto de entrada: valida o ambiente e abre a janela
├── app/             # Window: JFrame 1280×720 + GLCanvas + FPSAnimator (60 FPS)
├── render/          # Renderer (GLEventListener), SphereMesh (UV-sphere em VBO), MeshData
├── modelo/          # corpos celestes, catálogo de dados e relógio da simulação
├── camera/          # câmera orbital e controle de entrada (mouse/teclado)
├── math/            # Vec3 e Mat4 (column-major)
└── exception/       # AwtInternalsNotOpenException
```

| Componente | Responsabilidade |
|---|---|
| **Main / Window** | Ponto de entrada da aplicação. Cria a interface (Swing) e inicializa o contexto OpenGL com JOGL. |
| **Renderer** | Contém o loop principal (`display`), responsável por redesenhar a cena e aplicar iluminação e texturas. |
| **SphereMesh** | Gera a malha esférica (posições, normais, UVs e índices) uma única vez e a reutiliza para todos os corpos. |
| **Corpo celeste** | Abstrai os planetas e o Sol: raio, distância orbital, períodos de translação e rotação, inclinação do eixo e textura. |
| **Câmera** | Gerencia as matrizes de projeção e visualização, permitindo navegar pelo espaço 3D (pan, zoom e rotação). |
| **Vec3 / Mat4** | Operações vetoriais e matriciais usadas em toda a cena. |

---

## 🧮 Modelos Matemáticos

O projeto faz uso intenso de modelos matemáticos da computação gráfica:

- **Matrizes de translação e rotação** — calculam a posição de cada planeta em sua órbita com base no tempo contínuo. A matriz de modelo de cada corpo é composta hierarquicamente:

  ```
  M_planeta = M_pai · R_y(θ_órbita) · T(d, 0, 0) · [ R_z(inclinação) · R_y(θ_rotação) · S(r) ]
  ```

- **Ângulos em função do tempo** — o tempo simulado é medido em dias:

  ```
  θ_órbita(dias)  = 360° · (dias / períodoOrbitalDias)          mod 360
  θ_rotação(dias) = 360° · (dias · 24 / períodoRotaçãoHoras)    mod 360
  ```

- **Projeção perspectiva** — garante a profundidade realista do cenário (`fovY = 45°`, `near = 0.1`, `far = 1000`).
- **Matriz de visualização (`lookAt`)** — câmera orbital em coordenadas esféricas ao redor de um ponto alvo.
- **Primitivas geométricas** — geração de malha esférica (UV-sphere) para a renderização volumétrica dos planetas:

  ```
  φ = π · i / stacks,   θ = 2π · j / slices
  posição = ( sin φ · cos θ,  cos φ,  −sin φ · sin θ ),   normal = posição
  ```

> O detalhamento completo das fórmulas está em [`docs/escopo-sistema-solar.md`](docs/escopo-sistema-solar.md#3-modelos-matemáticos-detalhamento).

---

## 🚀 Como Executar

### Pré-requisitos

- **JDK 17** (ou superior), com a variável `JAVA_HOME` apontando para ele.
- **Maven não é necessário:** o projeto inclui o Maven Wrapper (`mvnw` / `mvnw.cmd`), que baixa o Maven 3.9.9 na primeira execução.
- O **JOGL 2.5.0** e os nativos de Windows, Linux e macOS são baixados automaticamente pelo Maven (repositório da JogAmp).
- Driver de vídeo com suporte ao perfil **OpenGL GL2**.

### Compilar, testar e executar

**Windows (PowerShell / cmd)**

```powershell
.\mvnw.cmd clean package       # compila, roda os testes e gera o JAR em target/
.\mvnw.cmd test                # roda apenas os testes (JUnit 5)
.\mvnw.cmd compile exec:java   # executa a aplicação
```

**Linux / macOS / Git Bash**

```bash
./mvnw clean package
./mvnw test
./mvnw compile exec:java
```

Para visualizar a malha em **modo wireframe**, passe o argumento `--wireframe`:

```bash
./mvnw compile exec:java -Dexec.args="--wireframe"
```

### Abrir na IDE

- **IntelliJ IDEA:** *File → Open* e selecione a pasta do projeto (ou o `pom.xml`). O projeto Maven é importado automaticamente.
- **Eclipse:** *File → Import → Maven → Existing Maven Projects* e selecione a pasta do projeto.

Execute a classe `br.unifacs.a3.sistemasolar.Main` adicionando nas **VM options** da configuração de execução:

```
--add-opens java.desktop/sun.awt=ALL-UNNAMED
```

> [!NOTE]
> O JOGL acessa classes internas do AWT por reflexão, e o Java 17 bloqueia esse acesso por padrão. Pelo `mvnw exec:java` a flag já é aplicada automaticamente (arquivo `.mvn/jvm.config`). Sem ela, a aplicação encerra com `AwtInternalsNotOpenException` explicando o que fazer.

---

## 🎮 Controles

> [!NOTE]
> Controles planejados para a versão final (em desenvolvimento).

| Entrada | Ação |
|---|---|
| Arrastar com botão esquerdo | Rotacionar a câmera |
| Arrastar com botão direito | Pan (mover o alvo da câmera) |
| Roda do mouse | Zoom |
| `0` – `8` | Focar no Sol (0) ou no planeta *n* |
| `Espaço` | Pausar / retomar a simulação |
| `+` / `-` | Acelerar / desacelerar o tempo |
| `O` | Mostrar / esconder órbitas |
| `L` | Ligar / desligar iluminação |
| `R` | Resetar câmera e tempo |
| `H` | Mostrar / esconder a ajuda na tela |
| `Esc` | Sair |

---

## 📚 Documentação

- [**Escopo técnico**](docs/escopo-sistema-solar.md) — decisões técnicas, diagrama de classes, detalhamento matemático, dados dos corpos celestes, backlog de tickets (`SS-XX`) e planejamento de sprints.
- Commits seguem o padrão [Conventional Commits](https://www.conventionalcommits.org/pt-br/), referenciando o ticket (ex.: `feat(render): ... [SS-04]`).

---

## 📊 Avaliação

Projeto referente à avaliação A3, contemplando:

| Entregável | Pontos | Status |
|---|:---:|:---:|
| Código-fonte em Java | 10 | ✅ |
| Relatório Técnico | 10 | ✅ |
| Apresentação | 10 | ⏳ |

---

## 📄 Licença

Distribuído sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
