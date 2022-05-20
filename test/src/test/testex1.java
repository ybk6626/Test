package test;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
public class testex1 extends Frame implements ActionListener {
	private TextField account, name, balance;
	private Button enter, next;
	private DataOutputStream output; // ���� ��Ʈ�� ��ü
	private DataInputStream input; // ���� �Է� ��Ʈ�� ��ü
	public testex1() {
		super( "������ ���� �� ����" );
		try {
			output = new DataOutputStream(new FileOutputStream("client.txt"));
			input = new DataInputStream(new FileInputStream("client.txt"));
		}catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		setSize(250, 130);
		setLayout( new GridLayout(4, 2));
		add ( new Label("���¹�ȣ"));
		account = new TextField(20); // ���¹�ȣ �Է� �ʵ� // 20�� �����־���!!
		add(account); 
		add(new Label("�̸�")); 
		name = new TextField(20); // �̸� �Է� �ʵ� // ���ĵ� �ɵ��ϴ�.
		add(name);
		add(new Label("�ܰ�"));
		balance = new TextField(20); // �ܰ� �Է� �ʵ�
		add(balance);
		enter = new Button("�Է�"); // �Էµ� �����͸� �����ϴ� ��ư.
		enter.addActionListener(this); // �̺�Ʈ�� ����
		add(enter);
		next = new Button("���"); // �Է��� �����ϴ� ��ư.
		next.addActionListener(this); // �̺�Ʈ�� ����
		add(next);
		addWindowListener(new WinListener());
		setVisible(true);
	}
	public void addRecord() {
		int accountNo = 0;
		String d;
		if ( !account.getText().equals("") ){ // ���¹�ȣ�� �Է��� üũ
			try {
				accountNo = Integer.parseInt(account.getText());
				if(accountNo > 0) {
					output.writeInt(accountNo); // ���¹�ȣ�� ������ ���Ͽ� �����Ѵ�.
					output.writeUTF(name.getText()); // �̸��� ���ڿ��� �����Ѵ�.
					d = balance.getText(); // �ܰ� ���ڿ��� ���� // �� �ܰ�� ���ڰ� �ƴ� ���ڿ��� �о���̴°�??
					output.writeDouble(Double.valueOf(d)); // �ܰ� �Ǽ��� �����Ѵ�. // ���ʿ� �Է��� ���ڿ��� �޾Ƶ��̱� �����̴�.
				}
				account.setText(""); // �ؽ�Ʈ �ʵ带 ����
				name.setText("");
				balance.setText("");
			}catch (NumberFormatException nfe) {
				System.err.println("ERR: ������ �Է��ؾ� �մϴ�.");
			}catch (IOException io) {
				System.err.println(io.toString());
				System.exit(1);
			}
		}
	}
	public void readRecord() {
		int clientNo, accountNo = 1;
		double d = 0.0;
		String namedata = "a";
		if ( !account.getText().equals("") ) {
			try {
				clientNo = Integer.parseInt(account.getText());
				if ( accountNo != clientNo) {
					while( accountNo != clientNo ) {
						accountNo = input.readInt(); // ���� ���� ���¹�ȣ�� �д´�.
						namedata = input.readUTF(); // ���ڿ��� �̸��� �д´�.
						d = input.readDouble(); // �Ǽ� ���� �ܰ� �д´�.
					}
				}
				else {
					accountNo = input.readInt();
					namedata = input.readUTF(); // ���ڿ��� �̸��� �д´�.
					d = input.readDouble(); // �Ǽ� ���� �ܰ� �д´�.
				}
			// �о���� �����͸� ���õ� �ؽ�Ʈ �ʵ忡 ����Ѵ�.
				account.setText(String.valueOf(accountNo));
				name.setText(namedata);
				balance.setText(String.valueOf(d));
			}catch(EOFException eof) {
				System.err.println("ERR: ������ ���� �����߽��ϴ�.");
				System.err.println("�߰� ���¹�ȣ ������ ���Ͻø� �����͸� �Է� ��, ����ٶ��ϴ�.");
				//closeFile();
			}catch(IOException io) {
				System.err.println(io.toString());
				System.exit(1);
			}
		}else System.err.println("ERR: ���¹�ȣ�� �Է��ؾ� �մϴ�.");
	}
	public void closeFile() { // ��Ʈ���� �ݰ� ���α׷��� �����Ѵ�.
		try {
			input.close();
			System.exit(0);
		}catch(IOException io) {
			System.err.println(io.toString());
			System.exit(1);
		}
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == enter)
			addRecord(); // �Էµ� �����͸� ���Ͽ� �����Ѵ�.
		else { // else if (e.getSource() == next) 
			readRecord(); // �����͸� �� ���ڵ徿 �д� �޼ҵ�
		}// else if -> else ���� �ۼ��ϰ� �Ǹ� else�� ������ �� �� ������ ��� ��Ʊ� ������ readRecord()�� �ϴ� ������ ���� ó��.
	}
	public static void main(String[] args) {
		new testex1();
	}
	class WinListener extends WindowAdapter{
		public void windowClosing(WindowEvent we) {
			System.exit(0);
		}
	}
}
