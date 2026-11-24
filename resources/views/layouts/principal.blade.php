<!DOCTYPE html>
<html lang="pt-BR" class="h-100">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('titulo', 'Dashboard') &mdash; Animalia</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="{{ asset('css/animalia.css') }}">
</head>
<body class="d-flex flex-column min-vh-100">

    <nav class="navbar navbar-expand-lg animalia-navbar">
        <div class="container">
            <a class="navbar-brand animalia-brand-nav" href="{{ route('dashboard') }}">Animalia</a>

            <button class="navbar-toggler" type="button"
                    data-bs-toggle="collapse" data-bs-target="#navbarPrincipal"
                    aria-controls="navbarPrincipal" aria-expanded="false" aria-label="Alternar navegação">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarPrincipal">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link {{ request()->routeIs('dashboard') ? 'active' : '' }}"
                           href="{{ route('dashboard') }}">Dashboard</a>
                    </li>
                </ul>

                <div class="navbar-nav align-items-lg-center gap-2">
                    <span class="nav-link text-light pe-none">
                        {{ Auth::user()->name }}
                    </span>
                    <form method="POST" action="{{ route('logout') }}">
                        @csrf
                        <button type="submit" class="btn btn-outline-light btn-sm">Sair</button>
                    </form>
                </div>
            </div>
        </div>
    </nav>

    <main class="container py-4 flex-grow-1">
        @include('partials.alertas')
        @yield('conteudo')
    </main>

    <footer class="animalia-footer py-3 mt-auto">
        <div class="container text-center">
            <small class="text-muted">
                Animalia &copy; {{ date('Y') }} &mdash; Sistema de Agendamento para Petshop
            </small>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
