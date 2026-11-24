<?php

use App\Http\Controllers\AutenticacaoController;
use App\Http\Controllers\CadastroController;
use App\Http\Controllers\DashboardController;
use Illuminate\Support\Facades\Route;

Route::redirect('/', '/login');

Route::middleware('guest')->group(function () {
    Route::get('/login', [AutenticacaoController::class, 'formularioLogin'])->name('login');
    Route::post('/login', [AutenticacaoController::class, 'entrar'])->name('autenticacao.entrar');

    Route::get('/cadastro', [CadastroController::class, 'formularioCadastro'])->name('cadastro.criar');
    Route::post('/cadastro', [CadastroController::class, 'salvar'])->name('cadastro.salvar');
});

Route::middleware('auth')->group(function () {
    Route::post('/logout', [AutenticacaoController::class, 'sair'])->name('logout');

    Route::get('/dashboard', [DashboardController::class, 'index'])->name('dashboard');
});
