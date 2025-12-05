# Rolê 📍

> O seu hub de eventos em tempo real em Manaus.

![Status do Projeto](https://img.shields.io/badge/Status-Em_Desenvolvimento-yellow)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple)
![Platform](https://img.shields.io/badge/Plataforma-Android-green)

## 📖 Sobre o Projeto

O **Rolê** é uma aplicação móvel que funciona como uma rede social de eventos, desenvolvida para conectar estabelecimentos ao público de Manaus. O sistema elimina a incerteza da escolha do lazer ao oferecer uma interface centralizada onde o usuário pode verificar a popularidade e o ambiente ("vibe") dos locais em tempo real.

Diferente de simples agendas culturais, o Rolê oferece um **"Termômetro Social"**: através de check-ins em tempo real, o usuário consegue visualizar a popularidade de um local antes de sair de casa

### O que o Rolê faz?
* Organiza um feed de eventos por relevância e data.
* Permite a descoberta de "rolês" com filtros de categoria, preço e local.
* Monitora a lotação dos ambientes através de contadores de check-ins.
* Oferece uma plataforma para estabelecimentos divulgarem seus eventos.


## 🚀 Funcionalidades Principais

### Para o Usuário (App Android)
- **Feed de Eventos:** Visualização de eventos de hoje e da semana para decisão rápida.
- **Filtros Avançados:** Busca por categoria (festa, bar, show, cultural), preço ou popularidade.
- **Check-in via Geolocalização:** Validação de presença no local do evento utilizando a câmera e geolocalização.
- **Detalhes do Evento:** Visualização de mapa, "vibe" do local e link externo para ingressos (quando aplikcável)

### Para o Estabelecimento (Portal Web)
- **Gestão de Eventos:** Cadastro, edição e exclusão de eventos via interface web.
- **Estatísticas:** Visualização de check-ins e popularidade dos eventos promovidos.
- **Promoções:** Criação de cupons e promoções para atrair público.


## 🛠 Tecnologias Utilizadas

O sistema foi projetado com uma arquitetura moderna cliente-servidor:

### Frontend (Mobile)
* **Linguagem:** Kotlin
* **Framework:** Jetpack Compose (para UI fluida e responsiva) 
* **Plataforma:** Android Studio
* **Padrão de Projeto:** MVVM com Clean Architecture

### Backend
* **Framework:** Ktor (Kotlin)
* **Comunicação:** API RESTful e WebSockets para dados em tempo real.
* **Banco de Dados:** H2 (Local) / MySQL (Produção).


## 🏗 Como executar

###Pré-requisitos
* JDK 17 ou superior
* Banco de dados: MySQL
* Android Studio com Emulador ou aparelho compatível

1. Clone o repositório
```bash
git clone https://github.com/lairaamarques/role-app.git
cd role-app
```

2. Instale as dependências
```bash
npm install
```

3. Inicie a aplicação em modo de desenvolvimento:
```bash
npm run dev
```

## 👥 Autores

Projeto desenvolvido como parte dos requisitos de especificação de software (2025)
* **Laira Klissia Marques de Lima** 
* **Messias Assunção Santos do Nascimento**
  

## 📄 Licença

Este projeto é destinado a fins acadêmicos e de portfólio.
