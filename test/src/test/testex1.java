package test;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
public class testex1 extends Frame implements ActionListener {
	private TextField account, name, balance;
	private Button enter, next;
	private DataOutputStream output; // 필터 스트림 객체
	private DataInputStream input; // 필터 입력 스트림 객체
	public testex1() {
		super( "고객파일 생성 및 읽음" );
		try {
			output = new DataOutputStream(new FileOutputStream("client.txt"));
			input = new DataInputStream(new FileInputStream("client.txt"));
		}catch (IOException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		setSize(250, 130);
		setLayout( new GridLayout(4, 2));
		add ( new Label("구좌번호"));
		account = new TextField(20); // 구좌번호 입력 필드 // 20이 빠져있었다!!
		add(account); 
		add(new Label("이름")); 
		name = new TextField(20); // 이름 입력 필드 // 합쳐도 될듯하다.
		add(name);
		add(new Label("잔고"));
		balance = new TextField(20); // 잔고 입력 필드
		add(balance);
		enter = new Button("입력"); // 입력된 데이터를 저장하는 버튼.
		enter.addActionListener(this); // 이벤트와 연결
		add(enter);
		next = new Button("출력"); // 입력을 종료하는 버튼.
		next.addActionListener(this); // 이벤트와 연결
		add(next);
		addWindowListener(new WinListener());
		setVisible(true);
	}
	public void addRecord() {
		int accountNo = 0;
		String d;
		if ( !account.getText().equals("") ){ // 구좌번호의 입력을 체크
			try {
				accountNo = Integer.parseInt(account.getText());
				if(accountNo > 0) {
					output.writeInt(accountNo); // 구좌번호를 정수로 파일에 저장한다.
					output.writeUTF(name.getText()); // 이름을 문자열로 저장한다.
					d = balance.getText(); // 잔고를 문자열로 읽음 // 왜 잔고는 숫자가 아닌 문자열로 읽어들이는가??
					output.writeDouble(Double.valueOf(d)); // 잔고를 실수로 저장한다. // 애초에 입력을 문자열로 받아들이기 때문이다.
				}
				account.setText(""); // 텍스트 필드를 삭제
				name.setText("");
				balance.setText("");
			}catch (NumberFormatException nfe) {
				System.err.println("ERR: 정수를 입력해야 합니다.");
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
						accountNo = input.readInt(); // 정수 값인 구좌번호를 읽는다.
						namedata = input.readUTF(); // 문자열인 이름을 읽는다.
						d = input.readDouble(); // 실수 값인 잔고를 읽는다.
					}
				}
				else {
					accountNo = input.readInt();
					namedata = input.readUTF(); // 문자열인 이름을 읽는다.
					d = input.readDouble(); // 실수 값인 잔고를 읽는다.
				}
			// 읽어들인 데이터를 관련된 텍스트 필드에 출력한다.
				account.setText(String.valueOf(accountNo));
				name.setText(namedata);
				balance.setText(String.valueOf(d));
			}catch(EOFException eof) {
				System.err.println("ERR: 파일의 끝에 도달했습니다.");
				System.err.println("추가 구좌번호 갱신을 원하시면 데이터를 입력 후, 종료바랍니다.");
				//closeFile();
			}catch(IOException io) {
				System.err.println(io.toString());
				System.exit(1);
			}
		}else System.err.println("ERR: 구좌번호를 입력해야 합니다.");
	}
	public void closeFile() { // 스트림을 닫고 프로그램을 종료한다.
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
			addRecord(); // 입력된 데이터를 파일에 저장한다.
		else { // else if (e.getSource() == next) 
			readRecord(); // 데이터를 한 레코드씩 읽는 메소드
		}// else if -> else 까지 작성하게 되면 else가 나오게 될 때 오류를 잡기 어렵기 때문에 readRecord()로 일단 보내서 오류 처리.
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
