@extends('layouts.visitante')

@section('titulo', 'Entrar')

@section('conteudo')
<div class="card shadow-sm">
    <div class="card-body p-4">
        <h1 class="h5 card-title text-center mb-4">Entrar</h1>

        <form method="POST" action="{{ route('autenticacao.entrar') }}" novalidate>
            @csrf

            <div class="mb-3">
                <label for="email" class="form-label">E-mail</label>
                <input
                    type="email"
                    id="email"
                    name="email"
                    class="form-control @error('email') is-invalid @enderror"
                    value="{{ old('email') }}"
                    autocomplete="email"
                    autofocus
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
                    autocomplete="current-password"
                    required
                >
                @error('password')
                    <div class="invalid-feedback">{{ $message }}</div>
                @enderror
            </div>

            <div class="mb-4 form-check">
                <input
                    type="checkbox"
                    class="form-check-input"
                    id="lembrar"
                    name="lembrar"
                >
                <label class="form-check-label" for="lembrar">Lembrar-me</label>
            </div>

            <div class="d-grid">
                <button type="submit" class="btn btn-primary">Entrar</button>
            </div>
        </form>
    </div>

    <div class="card-footer text-center py-3">
        <span class="text-muted small">Não tem conta?</span>
        <a href="{{ route('cadastro.criar') }}" class="small ms-1">Criar conta</a>
    </div>
</div>
@endsection
