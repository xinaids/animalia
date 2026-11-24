<?php

namespace Tests\Feature;

use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class AutenticacaoTest extends TestCase
{
    use RefreshDatabase;

    public function test_tela_login_exibe_formulario(): void
    {
        $response = $this->get(route('login'));

        $response->assertStatus(200);
        $response->assertViewIs('autenticacao.login');
    }

    public function test_login_valido_redireciona_para_dashboard(): void
    {
        $usuario = User::factory()->create();

        $response = $this->post(route('autenticacao.entrar'), [
            'email'    => $usuario->email,
            'password' => 'password',
        ]);

        $response->assertRedirect(route('dashboard'));
        $this->assertAuthenticatedAs($usuario);
    }

    public function test_login_invalido_retorna_erro_sem_autenticar(): void
    {
        User::factory()->create(['email' => 'usuario@exemplo.com']);

        $response = $this->post(route('autenticacao.entrar'), [
            'email'    => 'usuario@exemplo.com',
            'password' => 'senha-errada',
        ]);

        $response->assertRedirect();
        $response->assertSessionHasErrors('email');
        $this->assertGuest();
    }

    public function test_tela_cadastro_exibe_formulario(): void
    {
        $response = $this->get(route('cadastro.criar'));

        $response->assertStatus(200);
        $response->assertViewIs('autenticacao.cadastro');
    }

    public function test_cadastro_valido_autentica_e_redireciona_para_dashboard(): void
    {
        $response = $this->post(route('cadastro.salvar'), [
            'name'                  => 'Novo Usuário',
            'email'                 => 'novo@exemplo.com',
            'password'              => 'senhaSegura123',
            'password_confirmation' => 'senhaSegura123',
        ]);

        $response->assertRedirect(route('dashboard'));
        $this->assertAuthenticated();
        $this->assertDatabaseHas('users', ['email' => 'novo@exemplo.com']);
    }

    public function test_cadastro_invalido_email_duplicado_retorna_erro(): void
    {
        User::factory()->create(['email' => 'existente@exemplo.com']);

        $response = $this->post(route('cadastro.salvar'), [
            'name'                  => 'Outro Usuário',
            'email'                 => 'existente@exemplo.com',
            'password'              => 'senhaSegura123',
            'password_confirmation' => 'senhaSegura123',
        ]);

        $response->assertSessionHasErrors('email');
        $this->assertGuest();
    }

    public function test_cadastro_invalido_senha_curta_retorna_erro(): void
    {
        $response = $this->post(route('cadastro.salvar'), [
            'name'                  => 'Usuário Teste',
            'email'                 => 'usuario@exemplo.com',
            'password'              => '1234567',
            'password_confirmation' => '1234567',
        ]);

        $response->assertSessionHasErrors('password');
        $this->assertGuest();
    }

    public function test_dashboard_sem_autenticacao_redireciona_para_login(): void
    {
        $response = $this->get(route('dashboard'));

        $response->assertRedirect(route('login'));
    }

    public function test_logout_desloga_e_redireciona_para_login(): void
    {
        $usuario = User::factory()->create();
        $this->actingAs($usuario);

        $response = $this->post(route('logout'));

        $response->assertRedirect(route('login'));
        $this->assertGuest();
    }
}
