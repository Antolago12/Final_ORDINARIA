package org.vaadin.example;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.vaadin.example.model.Direccion;
import org.vaadin.example.model.MetodoPago;
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
        grid.addItemDoubleClickListener(event -> abrirDialogoDetalle(event.getItem()));


        Button generarPdfBtn = new Button("Generar PDF", event -> generarPdf());

        Button anadirUsuarioBtn = new Button("Añadir usuario", event -> abrirDialogoNuevoUsuario());

        add(grid, anadirUsuarioBtn, generarPdfBtn); // Asegúrate de que los botones están en orden
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
    private void abrirDialogoDetalle(Usuario usuario) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();

        layout.add(new H4("Datos de Usuario"));
        layout.add(new Span("Nombre: " + usuario.getNombre()));
        layout.add(new Span("Apellidos: " + usuario.getApellidos()));
        layout.add(new Span("NIF: " + usuario.getNif()));
        layout.add(new Span("Email: " + usuario.getEmail()));

        // Dirección
        Direccion dir = usuario.getDireccion();
        if (dir != null) {
            layout.add(new H4("Dirección"));
            layout.add(new Span("Calle: " + dir.getCalle()));
            layout.add(new Span("Número: " + dir.getNumero()));
            layout.add(new Span("Piso/Letra: " + dir.getPisoLetra()));
            layout.add(new Span("Código Postal: " + dir.getCodigoPostal()));
            layout.add(new Span("Ciudad: " + dir.getCiudad()));
        }

        // Método de pago
        MetodoPago mp = usuario.getMetodoPago();
        if (mp != null) {
            layout.add(new H4("Método de Pago"));
            layout.add(new Span("Número Tarjeta: " + mp.getNumeroTarjeta()));
            layout.add(new Span("Nombre Asociado: " + mp.getNombreAsociado()));
        }

        Button cerrarBtn = new Button("Cerrar", e -> dialog.close());
        layout.add(cerrarBtn);

        dialog.add(layout);
        dialog.open();
    }

    private void abrirDialogoNuevoUsuario() {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();

        // Campos para usuario
        TextField nombreField = new TextField("Nombre");
        TextField apellidosField = new TextField("Apellidos");
        TextField emailField = new TextField("Email");
        TextField nifField = new TextField("NIF");

        // Campos para dirección
        TextField calleField = new TextField("Calle");
        TextField numeroField = new TextField("Número");
        TextField pisoLetraField = new TextField("Piso/Letra");
        TextField codigoPostalField = new TextField("Código Postal");
        TextField ciudadField = new TextField("Ciudad");

        // Campos para método de pago
        TextField numeroTarjetaField = new TextField("Número de Tarjeta");
        TextField nombreAsociadoField = new TextField("Nombre Asociado");

        Button guardarBtn = new Button("Guardar", event -> {
            // Generar id aleatorio
            String id = java.util.UUID.randomUUID().toString();

            // Crear objetos auxiliares
            Direccion direccion = new Direccion(
                    calleField.getValue(),
                    parseIntOrZero(numeroField.getValue()),
                    codigoPostalField.getValue(),
                    pisoLetraField.getValue(),
                    ciudadField.getValue()
            );
            MetodoPago metodoPago = new MetodoPago(
                    parseLongOrZero(numeroTarjetaField.getValue()),
                    nombreAsociadoField.getValue()
            );

            Usuario usuario = new Usuario(
                    id,
                    nombreField.getValue(),
                    apellidosField.getValue(),
                    nifField.getValue(),
                    direccion,
                    emailField.getValue(),
                    metodoPago
            );

            // Llama al método que hace POST al backend
            anadirUsuarioEnBackend(usuario);

            dialog.close();
            cargarUsuarios();
        });

        Button cancelarBtn = new Button("Cancelar", event -> dialog.close());

        layout.add(
                new H4("Datos de Usuario"), nombreField, apellidosField, nifField, emailField,
                new H4("Dirección"), calleField, numeroField, pisoLetraField, codigoPostalField, ciudadField,
                new H4("Método de Pago"), numeroTarjetaField, nombreAsociadoField,
                guardarBtn, cancelarBtn
        );

        dialog.add(layout);
        dialog.open();
    }

    // Métodos auxiliares para parseo seguro:
    private int parseIntOrZero(String value) {
        try { return Integer.parseInt(value); } catch (NumberFormatException e) { return 0; }
    }
    private long parseLongOrZero(String value) {
        try { return Long.parseLong(value); } catch (NumberFormatException e) { return 0L; }
    }

    private void anadirUsuarioEnBackend(Usuario usuario) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            Gson gson = new Gson();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8081/usuarios")) // Ajusta el puerto si corresponde
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(usuario)))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            Notification.show("Usuario añadido correctamente.", 3000, Notification.Position.MIDDLE);
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error al añadir usuario.", 3000, Notification.Position.MIDDLE);
        }
    }




}
