package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class CifradoCesar {

    private static final String abecedario = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    public static String cifrar(int desplazamiento, String texto) {

        String resultado = "";

        for (int i = 0; i < texto.length(); i++) {

            char letra = texto.charAt(i);

            if (letra >= 'A' && letra <= 'Z') {

                letra = (char) ('A' + (letra - 'A' + desplazamiento) % 62);

            } else if (letra >= 'a' && letra <= 'z') {

                letra = (char) ('a' + (letra - 'a' + desplazamiento) % 62);
            }

            resultado += letra;
        }

        return resultado;
    }

    public static void main(String[] args) throws IOException, SQLException {

        String url = "jdbc:mysql://localhost:3306/prueba01_mysql";
        String usuario = "alumno";
        String contraseña = "123456";

        String insertar = "INSERT INTO cifrado (desplazamiento, texto, textoCifrado) VALUES (?, ?, ?)";

        try (Connection conexion = DriverManager.getConnection(url, usuario, contraseña);
             Statement st = conexion.createStatement()) {

            String eliminarTabla = "DROP TABLE IF EXISTS cifrado";

            st.executeUpdate(eliminarTabla);

            String crearTabla = "CREATE TABLE IF NOT EXISTS cifrado (" +
                    "desplazamiento INT, " +
                    "texto VARCHAR(1000) NOT NULL, " +
                    "textoCifrado VARCHAR(1000) NOT NULL)";

            st.executeUpdate(crearTabla);

            try (PreparedStatement ps = conexion.prepareStatement(insertar);
                 BufferedReader br = new BufferedReader(new FileReader("/home/tarde/Documentos/Acceso a datos/Prueba01/src/main/java/org/examples/texto.csv"))) {

                String linea;

                while ((linea = br.readLine()) != null) {

                    String[] campos = linea.split(";", 2);

                    int desplazamiento = Integer.parseInt(campos[0]);
                    String texto = campos[1];

                    ps.setInt(1, desplazamiento);
                    ps.setString(2, texto);
                    ps.setString(3, cifrar(desplazamiento, texto));

                    ps.addBatch();
                }

                ps.executeBatch();
            }

            String consulta = "SELECT desplazamiento, texto, textoCifrado FROM cifrado";

            System.out.println("\nDESPLAZAMIENTO | TEXTO ORIGINAL | TEXTO CIFRADO");

            ResultSet rs = st.executeQuery(consulta);

            while (rs.next()) {

                int desplazamiento = rs.getInt("desplazamiento");
                String texto = rs.getString("texto");
                String textoCifrado = rs.getString("textoCifrado");

                System.out.println(desplazamiento + " | " + texto + " | " + textoCifrado);
            }

            rs.close();
        }
    }
}
