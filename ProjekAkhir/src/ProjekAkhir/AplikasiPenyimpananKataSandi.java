package ProjekAkhir;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.SecureRandom;

public class AplikasiPenyimpananKataSandi {
    private Connection connection;

    public AplikasiPenyimpananKataSandi() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/KataSandi", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String buatKataSandiOtomatis(int panjang) {
        SecureRandom random = new SecureRandom();
        String karakter = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder kataSandi = new StringBuilder(panjang);
        for (int i = 0; i < panjang; i++) {
            int indexRandom = random.nextInt(karakter.length());
            kataSandi.append(karakter.charAt(indexRandom));
        }

        return kataSandi.toString();
    }

    // Metode untuk enkripsi menggunakan Caesar Cipher
    public String enkripsi(String teks, int shift) {
        StringBuilder hasil = new StringBuilder();

        for (char karakter : teks.toCharArray()) {
            if (Character.isLetterOrDigit(karakter)) {
                char batasAtas = Character.isDigit(karakter) ? '0' : (Character.isUpperCase(karakter) ? 'A' : 'a');
                hasil.append((char) ((karakter - batasAtas + shift) % 36 + batasAtas));
            } else {
                hasil.append(karakter);
            }
        }

        return hasil.toString();
    }

    // Metode untuk dekripsi menggunakan Caesar Cipher
    public String dekripsi(String teks, int shift) {
        return enkripsi(teks, 36 - shift);
    }

    public void simpanKataSandiKeFile(String nama, String kataSandi) {
        // Enkripsi nama dan kata sandi sebelum disimpan
        String namaTerenkripsi = enkripsi(nama, 3); // Misalnya menggunakan shift 3
        String kataSandiTerenkripsi = enkripsi(kataSandi, 3); // Misalnya menggunakan shift 3

        try {
            String query = "INSERT INTO simpan(Nama, KataSandi) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, namaTerenkripsi);
            preparedStatement.setString(2, kataSandiTerenkripsi);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Metode untuk mendekripsi data dari database dan menampilkannya
    public String tampilkanDataDariFile() {
        StringBuilder result = new StringBuilder();

        try {
            String query = "SELECT Nama, KataSandi FROM simpan";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String namaTerenkripsi = resultSet.getString("Nama");
                String kataSandiTerenkripsi = resultSet.getString("KataSandi");

                // Dekripsi nama dan kata sandi sebelum ditampilkan
                String namaDekripsi = dekripsi(namaTerenkripsi, 3); // Misalnya menggunakan shift 3
                String kataSandiDekripsi = dekripsi(kataSandiTerenkripsi, 3); // Misalnya menggunakan shift 3

                result.append("Nama: ").append(namaDekripsi).append("\n");
                result.append("Kata Sandi: ").append(kataSandiDekripsi).append("\n");
                result.append("--------------------\n");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result.toString();
    }
}

