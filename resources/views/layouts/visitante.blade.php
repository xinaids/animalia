<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('titulo', 'Animalia') &mdash; Animalia</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="{{ asset('css/animalia.css') }}">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center align-items-center min-vh-100 py-4">
            <div class="col-12 col-sm-10 col-md-7 col-lg-5 col-xl-4">
                <div class="text-center mb-4">
                    <a href="{{ route('login') }}" class="text-decoration-none">
                        <div class="animalia-logo-mark mx-auto mb-2"></div>
                        <span class="animalia-brand d-block">Animalia</span>
                    </a>
                    <p class="text-muted small mb-0">Sistema de Agendamento para Petshop</p>
                </div>

                @include('partials.alertas')

                @yield('conteudo')
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
