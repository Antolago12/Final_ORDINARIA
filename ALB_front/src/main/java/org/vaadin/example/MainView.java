package org.vaadin.example;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
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
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setSortable(true);
        grid.addColumn(Usuario::getApellidos).setHeader("Apellidos").setSortable(true);
        grid.addColumn(Usuario::getNif).setHeader("NIF").setSortable(true);
        grid.addColumn(Usuario::getEmail).setHeader("Email").setSortable(true);


        // Botón "Editar" en cada fila
        grid.addComponentColumn(usuario -> {
            Button editarBtn = new Button("Editar", click -> abrirDialogoEditar(usuario));
            return editarBtn;
        }).setHeader("Acciones");

        Button generarPdfBtn = new Button("Generar PDF", event -> generarPdf());

        add(grid, generarPdfBtn); // Así lo pones debajo del grid
        cargarUsuarios(); // Llama al metodo que hace el GETnera

    }

    private void generarPdf() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/usuarios/pdf")) // Ajusta el puerto si cambia
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Notification.show("PDF generado correctamente en el backend.", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error al generar el PDF.", 3000, Notification.Position.MIDDLE);
        }
    }



    // Metodo para obtener los usuarios usando HttpClient y Gson
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

    // Metodo para mostrar un diálogo modal para editar usuario
    private void abrirDialogoEditar(Usuario usuario) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();

        TextField nombreField = new TextField("Nombre", usuario.getNombre());
        TextField apellidosField = new TextField("Apellidos", usuario.getApellidos());
        TextField emailField = new TextField("Email", usuario.getEmail());
        TextField nifField = new TextField("NIF", usuario.getNif());

        Button guardarBtn = new Button("Guardar", event -> {
            usuario.setNombre(nombreField.getValue());
            usuario.setApellidos(apellidosField.getValue());
            usuario.setEmail(emailField.getValue());
            usuario.setNif(nifField.getValue());
            // Aquí harías un PUT al backend para actualizar el usuario
            // updateUsuarioEnBackend(usuario);
            dialog.close();
            cargarUsuarios(); // Refresca la tabla después de editar
        });

        Button cancelarBtn = new Button("Cancelar", event -> dialog.close());

        layout.add(nombreField, apellidosField, emailField, nifField, guardarBtn, cancelarBtn);
        dialog.add(layout);
        dialog.open();
    }

}
