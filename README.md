# GameVault

Aplicação Android para gerir uma coleção de jogos. Permite registar jogos, acompanhar o estado de cada um, dar avaliações e ver estatísticas da coleção. Os dados ficam guardados no dispositivo e a aplicação funciona sem internet (exceto na pesquisa online).

## Funcionalidades

- Adicionar, editar e eliminar jogos (com confirmação antes de eliminar)
- Campos: nome, plataforma, gênero, ano de lançamento, estado, avaliação, favorito, notas e capa
- Estados: Por Jogar, A Jogar e Concluído
- Avaliação de 1 a 5 estrelas e marcação de favoritos
- Alterar estado, avaliação e favorito diretamente no ecrã de detalhe
- Pesquisa por nome, ignorando maiúsculas e acentos
- Filtros por estado, plataforma, género e favoritos
- Ordenação por mais recentes, nome (A-Z / Z-A), ano (mais novo / mais antigo) e melhor avaliação
- Pesquisa online na API FreeToGame para preencher o formulário automaticamente
- Capa a partir da pesquisa online ou da galeria do telefone
- Estatísticas da coleção (total, favoritos, concluídos, distribuição por estado, avaliação média, plataformas e géneros mais frequentes, intervalo de anos)
- Tema claro, escuro ou igual ao do sistema

## Ecrãs

- **Lista de jogos**: ecrã inicial com pesquisa, filtros, ordenação e lista em cartões
- **Detalhe do jogo**: informações completas, alteração rápida de estado/avaliação/favorito, editar e eliminar
- **Formulário**: criação e edição de jogos, com validação dos campos
- **Pesquisa online**: bottom sheet com resultados da API (a partir de 2 letras, até 20 resultados)
- **Estatísticas**: resumo da coleção
- **Definições**: escolha do tema e informações da aplicação

## Requisitos

### Funcionais

1. Listar todos os jogos da coleção
2. Adicionar, editar e eliminar jogos
3. Ver o detalhe de um jogo
4. Pesquisar, filtrar e ordenar a lista
5. Pesquisar jogos online e usar os dados no formulário
6. Escolher uma imagem da galeria como capa
7. Mostrar estatísticas da coleção
8. Escolher e guardar o tema da aplicação

### Não funcionais

1. Funcionar sem internet, exceto na pesquisa online
2. Manter os dados depois de fechar a aplicação
3. Validar os campos do formulário antes de gravar
4. Não permitir o mesmo jogo duas vezes na mesma plataforma
5. Mostrar mensagem clara com opção de tentar novamente em caso de erro de rede
6. Suportar Android 8.0 (API 26) ou superior
7. Usar apenas ligações HTTPS

## Dados

Base de dados local SQLite (`game_vault.db`) criada com Room, com a tabela `games`:

| Campo | Tipo | Descrição |
|---|---|---|
| id | inteiro | Chave primária gerada automaticamente |
| name | texto | Nome do jogo (obrigatório, máx. 100) |
| platform | texto | Plataforma (obrigatório, máx. 50) |
| genre | texto | Género (obrigatório, máx. 50) |
| release_year | inteiro | Ano de lançamento (1950 até ano atual + 2) |
| status | texto | TO_PLAY, PLAYING ou COMPLETED |
| is_favorite | booleano | Indica se é favorito |
| rating | inteiro | Avaliação de 0 a 5 (0 = sem avaliação) |
| notes | texto | Notas livres (máx. 500) |
| cover_url | texto | Link ou ficheiro local da capa (opcional) |
| rawg_id | inteiro | Identificador na API externa (opcional) |
| created_at | inteiro | Data em que o jogo foi adicionado |

Outros dados guardados:

- **DataStore**: tema escolhido e última ordenação usada
- **Pasta interna `covers`**: imagens da galeria copiadas em JPEG (máximo 1280 px)

## Arquitetura

O projeto segue a arquitetura MVVM, organizado em camadas:

- `domain`: modelos, interfaces dos repositórios, regras de filtro/ordenação e validação
- `data`: Room (local), Retrofit (remote), DataStore (preferences), gestão de imagens (storage) e repositórios
- `di`: `AppContainer` com injeção de dependências manual
- `ui`: ecrãs, ViewModels, componentes, navegação e tema

## Tecnologias

- Kotlin
- Jetpack Compose e Material 3
- Navigation Compose com Kotlin Serialization
- ViewModel e Lifecycle
- Coroutines e Flow
- Room com KSP
- DataStore Preferences
- Retrofit, OkHttp e Kotlinx Serialization
- Coil
- Photo Picker do Android
- JUnit
- R8/ProGuard

## Como executar

1. Clonar o repositório
2. Abrir o projeto no Android Studio
3. Sincronizar o Gradle
4. Executar num dispositivo ou emulador com Android 8.0 (API 26) ou superior

## Créditos

Dados e imagens da pesquisa online fornecidos por [FreeToGame](https://www.freetogame.com).
