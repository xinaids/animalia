@extends('layouts.visitante')

@section('titulo', 'Criar conta')

@section('conteudo')
<div class="card shadow-sm">
    <div class="card-body p-4">
        <h1 class="h5 card-title text-center mb-4">Criar conta</h1>

        <form method="POST" action="{{ route('cadastro.salvar') }}" novalidate>
            @csrf

            <div class="mb-3">
                <label for="name" class="form-label">Nome</label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    class="form-control @error('name') is-invalid @enderror"
                    value="{{ old('name') }}"
                    autocomplete="name"
                    autofocus
                    required
                >
                @error('name')
                    <div class="invalid-feedback">{{ $message }}</div>
                @enderror
            </div>

            <div class="mb-3">
                <label for="email" class="form-label">E-mail</label>
                <input
                    type="email"
                    id="email"
                    name="email"
                    class="form-control @error('email') is-invalid @enderror"
                    value="{{ old('email') }}"
                    autocomplete="email"
                    required
                >
                @error('email')
                    <div class="invalid-feedback">{{ $message }}</div>
                @enderror
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">Senha</label>
                <input
                    type="password"
                    id="password"
                    name="password"
                    class="form-control @error('password') is-invalid @enderror"
                    autocomplete="new-password"
                    required
                >
                @error('password')
                    <div class="invalid-feedback">{{ $message }}</div>
                @enderror
                <div class="form-text">Mínimo de 8 caracteres.</div>
            </div>

            <div class="mb-4">
                <label for="password_confirmation" class="form-label">Confirmar senha</label>
                <input
                    type="password"
                    id="password_confirmation"
                    name="password_confirmation"
                    class="form-control"
                    autocomplete="new-password"
                    required
                >
            </div>

            <div class="d-grid">
                <button type="submit" class="btn btn-primary">Criar conta</button>
            </div>
        </form>
    </div>

    <div class="card-footer text-center py-3">
        <span class="text-muted small">Já tem conta?</span>
        <a href="{{ route('login') }}" class="small ms-1">Entrar</a>
    </div>
</div>
@endsection
