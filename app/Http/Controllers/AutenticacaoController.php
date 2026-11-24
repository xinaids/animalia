<?php

namespace App\Http\Controllers;

use App\Http\Requests\LoginRequest;
use Illuminate\Http\RedirectResponse;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\View\View;

class AutenticacaoController extends Controller
{
    public function formularioLogin(): View
    {
        return view('autenticacao.login');
    }

    public function entrar(LoginRequest $request): RedirectResponse
    {
        $credenciais = $request->only('email', 'password');
        $lembrar = $request->boolean('lembrar');

        if (Auth::attempt($credenciais, $lembrar)) {
            $request->session()->regenerate();

            return redirect()->intended(route('dashboard'));
        }

        return back()
            ->withInput($request->only('email'))
            ->withErrors(['email' => 'E-mail ou senha inválidos.']);
    }

    public function sair(Request $request): RedirectResponse
    {
        Auth::logout();

        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect()->route('login');
    }
}
