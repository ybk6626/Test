import qrcode
from pyzbar.pyzbar import decode
from PIL import Image, ImageTk, ImageDraw, ImageFont
import tkinter as tk
from tkinter import filedialog, messagebox
import pandas as pd


class QRCodeApp:
    def __init__(self, root):
        self.root = root
        self.root.title("QR Code Tool")
        self.root.state('zoomed')
        self.root.configure(bg="#2c3e50")

        # 기본 UI 설정
        header = tk.Label(root, text="QR Code Reader & Generator with Excel Lookup",
                          font=("Helvetica", 24, "bold"), bg="#1abc9c", fg="white", pady=15)
        header.pack(fill=tk.X)

        input_frame = tk.Frame(root, bg="#34495e", bd=2, relief="ridge")
        input_frame.pack(pady=20, padx=20, fill=tk.X)

        tk.Label(input_frame, text="Original Data", font=("Arial", 14, "bold"), bg="#34495e", fg="white").grid(row=0, column=0, sticky="w", padx=10, pady=10)
        self.original_data = tk.Text(input_frame, height=5, width=70, wrap=tk.WORD, bg="#ecf0f1", fg="#2c3e50")
        self.original_data.grid(row=1, column=0, columnspan=2, padx=10, pady=5)

        tk.Label(input_frame, text="Modified Data", font=("Arial", 14, "bold"), bg="#34495e", fg="white").grid(row=2, column=0, sticky="w", padx=10, pady=10)
        self.modified_data = tk.Text(input_frame, height=5, width=70, wrap=tk.WORD, bg="#ecf0f1", fg="#2c3e50")
        self.modified_data.grid(row=3, column=0, columnspan=2, padx=10, pady=5)

        button_frame = tk.Frame(root, bg="#2c3e50")
        button_frame.pack(pady=20)

        tk.Button(button_frame, text="Generate QR Code", command=self.generate_qr_code_with_excel, width=20,
                  bg="#3498db", fg="white", font=("Arial", 12, "bold")).grid(row=0, column=0, padx=10, pady=10)
        tk.Button(button_frame, text="Save QR Code", command=self.save_qr_code, width=20,
                  bg="#e67e22", fg="white", font=("Arial", 12, "bold")).grid(row=0, column=1, padx=10, pady=10)

        self.qr_image_label = tk.Label(root, text="QR Code will be displayed here", width=70, height=40, relief="groove",
                                       bg="#95a5a6", anchor="center", font=("Arial", 12))
        self.qr_image_label.pack(pady=20)

        self.generated_qr = None

    def generate_qr_code_with_excel(self):
        # Modified Data 읽기
        data = self.modified_data.get("1.0", tk.END).strip()
        if not data:
            messagebox.showwarning("Warning", "Please enter valid data to generate a QR code.")
            return

        try:
            # 엑셀 파일 선택
            file_path = filedialog.askopenfilename(filetypes=[("Excel Files", "*.xlsx;*.xls")])
            if not file_path:
                messagebox.showwarning("Warning", "No Excel file selected.")
                return

            # 엑셀 파일 읽기
            df = pd.read_excel(file_path)

            # Modified Data로 엑셀 데이터 검색
            matched_rows = df[df.apply(lambda row: row.astype(str).str.contains(data, na=False).any(), axis=1)]

            if matched_rows.empty:
                messagebox.showinfo("Info", "No matching data found in the Excel file.")
                return

            # 검색된 데이터 문자열 생성
            excel_data = matched_rows.to_string(index=False, header=True)

            # QR 코드 데이터 생성
            qr = qrcode.QRCode(version=1, box_size=10, border=4)
            qr.add_data(data)
            qr.make(fit=True)

            # QR 코드 이미지 생성
            qr_img = qr.make_image(fill_color="black", back_color="white").convert("RGB")

            # 텍스트 크기 계산
            draw = ImageDraw.Draw(qr_img)
            try:
                font = ImageFont.truetype("arial.ttf", 16)
            except IOError:
                font = ImageFont.load_default()
            text_width, text_height = draw.textsize(excel_data, font=font)
            qr_width, qr_height = qr_img.size

            total_width = max(qr_width, text_width + 20)
            total_height = qr_height + text_height + 20

            combined_img = Image.new("RGB", (total_width, total_height), "white")
            combined_img.paste(qr_img, (0, text_height + 10))
            draw = ImageDraw.Draw(combined_img)
            draw.text((10, 10), excel_data, fill="black", font=font)

            # 이미지 업데이트
            self.qr_image = ImageTk.PhotoImage(combined_img)
            self.qr_image_label.config(image=self.qr_image, text="")
            self.generated_qr = combined_img
        except Exception as e:
            messagebox.showerror("Error", f"An error occurred: {str(e)}")

    def save_qr_code(self):
        if self.generated_qr:
            file_path = filedialog.asksaveasfilename(defaultextension=".png", filetypes=[("PNG Files", "*.png")])
            if file_path:
                try:
                    self.generated_qr.save(file_path)
                    messagebox.showinfo("Success", "QR Code saved successfully!")
                except Exception as e:
                    messagebox.showerror("Error", f"Failed to save QR Code: {str(e)}")
            else:
                messagebox.showwarning("Warning", "Save operation was canceled.")
        else:
            messagebox.showwarning("Warning", "No QR code to save. Please generate one first.")


if __name__ == "__main__":
    root = tk.Tk()
    app = QRCodeApp(root)
    root.mainloop()
