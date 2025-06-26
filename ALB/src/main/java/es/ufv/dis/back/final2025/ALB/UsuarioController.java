package es.ufv.dis.back.final2025.ALB;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // GET /usuarios - Lista todos los usuarios
    @GetMapping
    public List<Usuario> getUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    // GET /usuarios/{id} - Busca usuario por ID
    @GetMapping("/{id}")
    public Usuario getUsuario(@PathVariable String id) {
        return usuarioService.getUsuarioById(id);
    }

    // POST /usuarios - Añade usuario nuevo
    @PostMapping
    public void addUsuario(@RequestBody Usuario usuario) {
        usuarioService.addUsuario(usuario);
    }

    // PUT /usuarios/{id} - Edita usuario existente
    @PutMapping("/{id}")
    public void updateUsuario(@PathVariable String id, @RequestBody Usuario usuario) {
        usuarioService.updateUsuario(id, usuario);
    }

    // En UsuarioController.java (backend)
    @GetMapping("/pdf")
    public void generarPdf(HttpServletResponse response) {
        usuarioService.generarPdf(response);
    }


}
