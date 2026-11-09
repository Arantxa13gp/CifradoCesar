import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.*;


public static void main(String[] args) throws SQLException {

        String url = "jdbc:mysql://localhost:3306/prueba01_mysql";
        String usuario = "alumno";
        String contraseña = "123456";
        String insertar = "INSERT INTO alumno (id, nombre) VALUES (?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
             Statement st = conexion.createStatement();
             PreparedStatement ps = conexion.prepareStatement(insertar);
        ) {
            String eliminarTabla = "DROP TABLE IF EXISTS alumno";

            st.executeUpdate(eliminarTabla);

            String crearTabla = "CREATE TABLE IF NOT EXISTS alumno (" +
                    "id INT PRIMARY KEY, " +
                    "nombre VARCHAR(50))";

            st.executeUpdate(crearTabla);

            BufferedReader br = new BufferedReader(new FileReader("/home/tarde/Documentos/Prueba01/src/main/java/org/examples/alumnos.csv"));
            String linea;
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(",");
                String id = campos[0];
                String nombre = campos[1];

                ps.setInt(1, Integer.parseInt(id));
                ps.setString(2, nombre);
                ps.addBatch();
            }

            String consulta = "SELECT id, nombre FROM alumno";

            ps.executeBatch();{
            System.out.println("\nListado de alumnos:");
            ResultSet rs = ps.executeQuery(consulta);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");

                    System.out.println(id + " - " + nombre);
            }
                rs.close();
        }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
}