package org.vaadin.example;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.vaadin.example.model.Usuario;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.List;

// Importa tus modelos
// import org.vaadin.example.model.Usuario; // O tu paquete

@Route("")
public class MainView extends VerticalLayout {

    private Grid<Usuario> grid;
    private Gson gson = new Gson();
    private final String API_URL = "http://localhost:8081/usuarios"; // Ajusta el puerto si lo cambias

    public MainView() {
        grid = new Grid<>(Usuario.class, false);

        // Configura las columnas manualmente para personalizar nombres
        grid.addColumn(Usuario::getNombre).setHeader("Nombre");
        grid.addColumn(Usuario::getApellidos).setHeader("Apellidos");
        grid.addColumn(Usuario::getNif).setHeader("NIF");
        grid.addColumn(Usuario::getEmail).setHeader("Email");

        // Botón "Editar" en cada fila
        grid.addComponentColumn(usuario -> {
            Button editarBtn = new Button("Editar", click -> abrirDialogoEditar(usuario));
            return editarBtn;
        }).setHeader("Acciones");

        add(grid);

        cargarUsuarios(); // Llama al método que hace el GET
    }

    // Método para obtener los usuarios usando HttpClient y Gson
    private void cargarUsuarios() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Usuario> usuarios = gson.fromJson(response.body(), new TypeToken<List<Usuario>>() {}.getType());
            grid.setItems(usuarios);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para mostrar un diálogo modal para editar usuario
    private void abrirDialogoEditar(Usuario usuario) {
        Dialog dialog = new Dialog();
        dialog.add("Aquí iría el formulario de edición para " + usuario.getNombre());
        dialog.open();
    }
}
