#  Sistema de Gestão de Farmácia Hospitalar - Backend

![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.5-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

API RESTful completa para o gerenciamento de estoque de uma farmácia hospitalar. O sistema foi projetado para ser robusto, seguro e escalável, cobrindo as principais operações do dia a dia, desde o recebimento de mercadorias até a dispensação para os setores do hospital.

##  Features Principais

-   **Gestão de Itens:** CRUD completo para Medicamentos e Insumos, utilizando estratégia de herança (`SINGLE_TABLE`) com JPA/Hibernate.
-   **Controle de Estoque (FEFO):** Gerenciamento de múltiplos lotes por item, com controle de data de validade e baixa de estoque seguindo a regra "Primeiro que Vence, Primeiro que Sai" (FEFO).
-   **Registro de Movimentações em Lote:** Endpoints para registrar entradas e saídas de múltiplos itens em uma única transação, refletindo o fluxo de trabalho real de uma farmácia.
-   **Segurança com JWT:** Autenticação e autorização baseadas em token JWT, implementadas com Spring Security, garantindo que os endpoints sejam acessados apenas por usuários autenticados e com as permissões corretas.
-   **Geração de Relatórios PDF:** Endpoint para a geração de relatórios dinâmicos (ex: Saídas Diárias por Setor) utilizando a biblioteca iText.
-   **Dashboard de BI:** Endpoints que fornecem dados agregados para alimentar um dashboard de Business Intelligence, incluindo estatísticas, alertas de estoque baixo e lotes próximos ao vencimento.
-   **Consulta com IA (em desenvolvimento):** Exploração do Spring AI para permitir consultas ao sistema em linguagem natural.

##  Tecnologias Utilizadas

#### **Backend**
* **Java 21+**
* **Spring Boot 3.5.5**
* **Spring Web:** Para a criação dos controllers e endpoints RESTful.
* **Spring Data JPA / Hibernate:** Para a persistência de dados e mapeamento objeto-relacional.
* **Spring Security:** Para o controle de autenticação e autorização.
* **Lombok:** Para reduzir código boilerplate nas entidades e DTOs.
* **Maven:** Para gerenciamento de dependências.

#### **Banco de Dados**
* **PostgreSQL:** Banco de dados relacional.

#### **Segurança**
* **JWT (JSON Web Tokens):** Para a implementação de uma API stateless.
* **BCrypt:** Para a criptografia de senhas.

#### **Relatórios**
* **iText 7:** Para a geração de documentos PDF.

#### **DevOps**
* **Render.com:** Plataforma de nuvem para o deploy da aplicação e do banco de dados.
* **Git & GitHub:** Para versionamento de código.

##  Como Executar o Projeto

### Pré-requisitos
-   JDK 21 ou superior.
-   Maven 3.8 ou superior.
-   Uma instância do PostgreSQL rodando localmente ou na nuvem.
-   (Opcional) Uma chave de API da OpenAI para as funcionalidades de IA.

### Configuração do Ambiente
1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/sistema-farmacia-backend.git](https://github.com/seu-usuario/sistema-farmacia-backend.git)
    cd sistema-farmacia-backend
    ```

2.  **Configure as variáveis de ambiente:**
    * Na pasta `src/main/resources/`, renomeie o arquivo `application.properties.example` para `application.properties`.
    * Abra o novo arquivo `application.properties` e preencha as informações de conexão com o seu banco de dados local e outras chaves necessárias:
        ```properties
        # BANCO DE DADOS
        spring.datasource.url=jdbc:postgresql://localhost:5432/sua_base_de_dados
        spring.datasource.username=seu_usuario
        spring.datasource.password=sua_senha

        # JWT SECRET
        api.security.token.secret=seu_segredo_jwt_aqui

        # SPRING AI (OPCIONAL)
        spring.ai.openai.api-key=sua_chave_api_openai
        ```

### Executando a Aplicação
-   **Via Maven:**
    ```bash
    mvn spring-boot:run
    ```
-   **Via IDE:**
    * Importe o projeto como um projeto Maven na sua IDE (IntelliJ, Eclipse, etc.).
    * Execute a classe principal `SistemaFarmaciaSbApplication.java`.

A API estará disponível em `http://localhost:8080`.

##  Estrutura da API (Principais Endpoints)

-   `POST /api/auth/login`: Autentica um usuário e retorna um token JWT.
-   `GET, POST, PUT /api/itens`: Gerenciamento completo de itens (medicamentos e insumos).
-   `POST /api/movimentacoes/entrada`: Registra a entrada de um ou mais itens no estoque.
-   `POST /api/movimentacoes/saida`: Registra a saída de um ou mais itens do estoque para um setor.
-   `GET /api/estoque`: Consulta os saldos de estoque.
-   `GET /api/estoque/item/{itemId}`: Lista todos os lotes de um item específico.
-   `PATCH /api/estoque/ajustar/{loteId}`: Ajusta a quantidade/validade de um lote.
-   `GET /api/dashboard/stats`: Retorna as principais estatísticas para o dashboard.

---
_Este projeto foi desenvolvido para o hospital da minha cidade, demonstrando competências em tecnologias backend modernas e experiência com o cliente._
