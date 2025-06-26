package es.ufv.dis.back.final2025.ALB;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//----------PDF
import java.io.FileOutputStream;
import java.io.IOException;
//----------PDF

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

    // Si quieres, metodo para recargar la lista desde el fichero:
    public void recargarDesdeFichero() {
        usuarios = cargarUsuarios();
    }

    public void generarPdf(HttpServletResponse response) {
        Document doc = new Document(PageSize.A4, 50, 50, 100, 72);
        try {
            // Guarda el PDF en la raíz del backend
            PdfWriter.getInstance(doc, new FileOutputStream("info.pdf"));
            doc.open();

            // Título
            Paragraph titulo = new Paragraph("Listado de Usuarios");
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);
            doc.add(new Paragraph(" ")); // Espacio

            // Tabla con columnas relevantes
            PdfPTable table = new PdfPTable(5); // Número de columnas visibles (ajusta si quieres)
            table.addCell("Nombre");
            table.addCell("Apellidos");
            table.addCell("NIF");
            table.addCell("Email");
            table.addCell("Ciudad");

            for (Usuario usuario : getAllUsuarios()) {
                table.addCell(usuario.getNombre());
                table.addCell(usuario.getApellidos());
                table.addCell(usuario.getNif());
                table.addCell(usuario.getEmail());
                table.addCell(usuario.getDireccion().getCiudad());
            }

            doc.add(table);

            doc.close();

            // Opcional: Puedes escribir en el response, pero para el enunciado basta con guardar el PDF.
            // response.getWriter().write("PDF generado correctamente");

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

}
