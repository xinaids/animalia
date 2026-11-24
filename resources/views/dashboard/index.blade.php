@extends('layouts.principal')

@section('titulo', 'Dashboard')

@section('conteudo')
<div class="mb-4">
    <h2 class="h4 mb-1">Bem-vindo, {{ Auth::user()->name }}!</h2>
    <p class="text-muted mb-0">Resumo do sistema</p>
</div>

<div class="row g-4">
    <div class="col-12 col-sm-6 col-xl-3">
        <div class="card animalia-card-modulo h-100">
            <div class="card-body">
                <h5 class="card-title">Agendamentos</h5>
                <p class="card-text text-muted small mb-0">Em construção</p>
            </div>
        </div>
    </div>

    <div class="col-12 col-sm-6 col-xl-3">
        <div class="card animalia-card-modulo h-100">
            <div class="card-body">
                <h5 class="card-title">Tutores</h5>
                <p class="card-text text-muted small mb-0">Em construção</p>
            </div>
        </div>
    </div>

    <div class="col-12 col-sm-6 col-xl-3">
        <div class="card animalia-card-modulo h-100">
            <div class="card-body">
                <h5 class="card-title">Pets</h5>
                <p class="card-text text-muted small mb-0">Em construção</p>
            </div>
        </div>
    </div>

    <div class="col-12 col-sm-6 col-xl-3">
        <div class="card animalia-card-modulo h-100">
            <div class="card-body">
                <h5 class="card-title">Serviços</h5>
                <p class="card-text text-muted small mb-0">Em construção</p>
            </div>
        </div>
    </div>
</div>
@endsection
