package es.ufv.dis.back.final2025.ALB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final String FILE_PATH;
    private List<Usuario> usuarios;

    public UsuarioService() {
        // Esta ruta apunta a target/classes/usuarios.json después de compilar
        URL resource = getClass().getClassLoader().getResource("usuarios.json");
        if (resource != null) {
            FILE_PATH = resource.getPath();
        } else {
            // Si no existe, usamos una ruta temporal para que no falle
            FILE_PATH = "usuarios.json";
        }
        usuarios = cargarUsuarios();
    }

    private List<Usuario> cargarUsuarios() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Usuario>>(){}.getType();
            List<Usuario> userList = gson.fromJson(reader, listType);
            return userList != null ? userList : new ArrayList<>();
        } catch (FileNotFoundException e) {
            // Si el archivo no existe, devolvemos una lista vacía
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void guardarUsuarios() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(usuarios, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Usuario> getAllUsuarios() {
        return usuarios;
    }

    public Usuario getUsuarioById(String id) {
        Optional<Usuario> user = usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
        return user.orElse(null);
    }

    public void addUsuario(Usuario usuario) {
        usuarios.add(usuario);
        guardarUsuarios();
    }

    public void updateUsuario(String id, Usuario usuarioEditado) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId().equals(id)) {
                usuarios.set(i, usuarioEditado);
                guardarUsuarios();
                return;
            }
        }
    }

    // Si quieres, método para recargar la lista desde el fichero:
    public void recargarDesdeFichero() {
        usuarios = cargarUsuarios();
    }
}
