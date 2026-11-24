<?php

namespace App\Services;

use App\Models\User;

class CadastroService
{
    public function registrar(array $dados): User
    {
        return User::create([
            'name'     => $dados['name'],
            'email'    => $dados['email'],
            'password' => $dados['password'],
        ]);
    }
}
