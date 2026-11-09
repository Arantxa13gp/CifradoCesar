import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.*;


public static void main(String[] args) throws SQLException {

    String url = "jdbc:mysql://localhost:3306/prueba01_mysql";
    String usuario = "alumno";
    String contraseña = "123456";
    String insertar = "INSERT INTO banco (id, saldo) VALUES (?, ?)";

    try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
         Statement st = conexion.createStatement();
         PreparedStatement ps = conexion.prepareStatement(insertar);
    ) {
        String eliminarTabla = "DROP TABLE IF EXISTS banco";

        st.executeUpdate(eliminarTabla);

        String crearTabla = "CREATE TABLE IF NOT EXISTS banco (" +
                "id INT PRIMARY KEY, " +
                "saldo DOUBLE)";

        st.executeUpdate(crearTabla);

        BufferedReader br = new BufferedReader(new FileReader("/home/tarde/Documentos/Prueba01/src/main/java/org/examples/banco.csv"));
        String linea;
        br.readLine();

        while ((linea = br.readLine()) != null) {
            String[] campos = linea.split(",");
            String id = campos[0];
            String saldo = campos[1];

            ps.setInt(1, Integer.parseInt(id));
            ps.setString(2, "saldo");
            ps.addBatch();
        }

        String consulta = "SELECT id, saldo FROM bando";

        ps.executeBatch();{
            System.out.println("\nListado de saldos:");
            ResultSet rs = ps.executeQuery(consulta);
            while (rs.next()) {
                int id = rs.getInt("id");
                String saldo = rs.getString("saldo");

                System.out.println(id + " - " + saldo);
            }
            rs.close();
        }

    } catch (FileNotFoundException e) {
        throw new RuntimeException(e);

    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}