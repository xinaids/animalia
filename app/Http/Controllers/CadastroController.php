<?php

namespace App\Http\Controllers;

use App\Http\Requests\CadastroRequest;
use App\Services\CadastroService;
use Illuminate\Http\RedirectResponse;
use Illuminate\Support\Facades\Auth;
use Illuminate\View\View;

class CadastroController extends Controller
{
    public function __construct(private CadastroService $cadastroService) {}

    public function formularioCadastro(): View
    {
        return view('autenticacao.cadastro');
    }

    public function salvar(CadastroRequest $request): RedirectResponse
    {
        $usuario = $this->cadastroService->registrar($request->validated());

        Auth::login($usuario);

        return redirect()->route('dashboard');
    }
}
