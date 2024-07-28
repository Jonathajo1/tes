import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option = -1;

        while (option != 0) {
            System.out.println("Pilih opsi:");
            System.out.println("1. Cek Saldo");
            System.out.println("2. Input Saldo");
            System.out.println("3. Rekap Transaksi Perbulan");
            System.out.println("4. Transaksi antar Bank");
            System.out.println("0. Keluar");

            try {
                option = scanner.nextInt();

                switch (option) {
                    case 1:
                        System.out.print("Masukkan nomor rekening: ");
                        String accountNumber = scanner.next();
                        System.out.print("Masukkan bank ID: ");
                        int bankId = scanner.nextInt();
                        BankAccount account = new BankAccount(accountNumber, bankId);
                        System.out.println("Saldo: " + account.getBalance());
                        break;

                    case 2:
                        System.out.print("Masukkan nomor rekening: ");
                        accountNumber = scanner.next();
                        System.out.print("Masukkan bank ID: ");
                        bankId = scanner.nextInt();
                        System.out.print("Masukkan jumlah yang ingin ditambahkan: ");
                        double amount = scanner.nextDouble();
                        account = new BankAccount(accountNumber, bankId);
                        account.deposit(amount);
                        System.out.println("Saldo berhasil ditambahkan.");
                        break;

                    case 3:
                        System.out.println("Fitur ini belum diimplementasikan.");
                        break;

                    case 4:
                        System.out.print("Masukkan nomor rekening pengirim: ");
                        String sourceAccountNumber = scanner.next();
                        System.out.print("Masukkan bank ID pengirim: ");
                        int sourceBankId = scanner.nextInt();

                        System.out.print("Masukkan nomor rekening penerima: ");
                        String targetAccountNumber = scanner.next();
                        System.out.print("Masukkan bank ID penerima: ");
                        int targetBankId = scanner.nextInt();

                        System.out.print("Masukkan jumlah yang ingin ditransfer: ");
                        double transferAmount = scanner.nextDouble();

                        BankAccount sourceAccount = new BankAccount(sourceAccountNumber, sourceBankId);
                        BankAccount targetAccount = new BankAccount(targetAccountNumber, targetBankId);

                        sourceAccount.transfer(targetAccount, transferAmount);
                        System.out.println("Transfer berhasil.");
                        break;

                    case 0:
                        System.out.println("Keluar dari aplikasi.");
                        break;

                    default:
                        System.out.println("Opsi tidak valid.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Input tidak valid. Masukkan angka.");
                scanner.next(); // Bersihkan buffer input
            } catch (SQLException e) {
                System.out.println("Terjadi kesalahan database.");
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}
