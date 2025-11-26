package com.example.bt1;

import com.example.bt1.models.Comment;
import com.example.bt1.models.Product;
import com.example.bt1.models.User;

import java.util.ArrayList;
import java.util.List;

public class global {
    public static List<Product> list;

    private static List<User> list2;

    private List<Comment> list3;

    /*
    static {
        // hard code danh sách sản phẩm
        list = new ArrayList<>();
        list.add(new Product("Viên uống LéAna Ocavill hỗ trợ cân bằng nội tiết tố", "PHYTOPHARMA LTD",
                680000, 10000, R.drawable.leanaocavill, false, 5, true, "Bulgaria", "Cân bằng nội tiết tố",
                "Viên nang mềm", "Hộp 60 Viên", "Tinh dầu hoa anh thảo, Vitamin E, Nhân Sâm, Trinh nữ, Chiết xuất Maca",
                3.5f, "Léana Ocavill là loại thực phẩm giúp cân bằng nội tiết tố của thương hiệu Ocavill được nhập khẩu từ Bulgaria. Với sự kết hợp tinh dầu hoa anh thảo cùng các thành phần như rễ maca, nhân sâm, trinh nữ châu Âu và vitamin E, Léana Ocavill giúp cải thiện sức khỏe nữ giới hiệu quả.\n" +
                "\n" +
                "Viên Uống Léana Ocavill Hỗ Trợ Cân Bằng Nội Tiết Tố", "Uống 2 viên/ngày, trong hoặc ngay sau bữa ăn.",
                "Chưa có thông tin về tác dụng phụ của sản phẩmю", "Phụ nữ trưởng thành mong muốn hạn chế quá trình lão hóa, làm đẹp da." + "\n" + "Phụ nữ trong thời kỳ tiền mãn kinh, mãn kinh suy giảm nội tiết tố."));

        list.add(new Product("Viên uống Immuvita Easylife bổ sung vitamin và khoáng chất cho cơ thể, tăng sức khỏe",
                "C. HEDENKAMP GMBH & CO. KG", 390000, 21000, R.drawable.vitamin,
                true, 10, true, "Đức", "Vitamin tổng hợp",
                "Viên nén", "Hộp 100 Viên", "Canxi hydrogen phosphat, Magie oxide, Canxi carbonat, Vitamin C, Vitamin E, Niacin, Sắt fumarate, Panthothenic Acid, Lutein, Vitamin A, Đồng sulfat, Kẽm (kẽm oxit), Mangane Sulphate, Vitamin D3, Vitamin B6, Vitamin K1, Vitamin B2, Vitamin B1, Coenzyme Q10, Vitamin B12, Folic Acid, Crom (III) Chloride, Kali lodide, Natri Molybdate, Natri selenite, Biotin",
                4.5f, "Easylife Immuvita là thực phẩm bảo vệ sức khỏe giúp tăng cường sức khỏe dành cho người trưởng thành. Sản phẩm giúp bổ sung 26 vitamin và khoáng chất thiết yếu giúp nâng cao tinh thần và tăng cường chức năng miễn dịch của cơ thể.",
                "Người trưởng thành: Uống 1 viên ngày với 1 lượng nước vừa đủ.", "Chưa có thông tin về tác dụng phụ của sản phẩm.",
                "Thích hợp dùng cho người trưởng thành."));

        list.add(new Product("Viên uống giảm ho Nano Anpacov Biochempha", "Chi nhánh Công ty CP Dược Nature Việt Nam - nhà máy Nature Lifecare",
                149000, 16000, R.drawable.anpacov, true, 10, true, "Việt Nam", "Hô hấp, ho, xoang",
                "Viên nang cứng", "Hộp 60 Viên", "Nano Cao xuyên tâm liên, Nano Cao gừng, Nano Cao húng chanh, Nano Cao lá thường xuân",
                4.0f, "", "Uống 1 viên/lần x 2 lần/ngày, trước bữa ăn 30 phút hoặc sau khi ăn một giờ.",
                "Chưa có thông tin về tác dụng phụ của sản phẩm.",
                "Người từ 12 tuổi trở lên bị ho, ho có đờm, đau rát họng, khan tiếng do viêm họng, viêm phế quản."));

        list.add(new Product("Cốm vi sinh bổ sung lợi khuẩn đường ruột Lacto Biomin Gold+ New Hdpharma", "HD PHARMA",
                680000, 10000, R.drawable.lactobiomin, false, 5, true, "Việt Nam", "Hỗ trợ tiêu hóa",
                "Cốm", "Hộp 20 Gói x 5g", "Balcillus clausii, Bacillus subtilis, Bacillus coagulans, Inulin, Canxi gluconat, L-Lysine hydrochloride, Taurine, Betaglucan 80%, Sắt III Hydroxide Polymaltose complex, Vitamin B3, Thymomodulin, Magie gluconat, Kẽm Gluconat, Vitamin B5, Vitamin B1, Vitamin B2, Vitamin B6, Mangan sulfat",
                4.8f, "", "Trẻ từ 6 tháng đến 1 tuổi: 1 gói/ngày.\n" +
                "Trẻ từ 1 - 3 tuổi: Uống 1 gói/ lần, 2 lần/ ngày.\n" +
                "Trẻ từ 4 - 9 tuổi: Uống 1 gói/ lần, 2 - 3 lần/ ngày.\n" +
                "Trẻ từ 10 tuổi, người lớn: Uống 2 gói/ lần, 2 lần/ ngày.\n" +
                "Có thể cho trẻ ăn trực tiếp hoặc pha với nước nguội dưới 40°C. Uống trước hoặc sau ăn.",
                "Chưa có thông tin về tác dụng phụ của sản phẩm.", "Trẻ em và người lớn có sức đề kháng kém, hay ốm yếu, chán ăn, ăn không ngon, kém hấp thu, chậm lớn.\n" +
                "\n" +
                "Trẻ em và người lớn có biểu hiện khó tiêu, phân sống, tiêu chảy do loạn khuẩn đường ruột.\n" +
                "\n" +
                "Người có biểu hiện táo bón.\n" +
                "\n" +
                "Người sử dụng kháng sinh gây loạn khuẩn đường ruột."));

        // hard code danh sách người dùng
        list2 = new ArrayList<>();
        User u1 = new User();
        u1.setId(1);
        u1.setEmail("nhan1@gmail.com");
        u1.setPassword("123456");
        u1.setFullName("Le Nhan");
        u1.setPhone("0123456");
        u1.setAddress("123 Nguyen Hue, Q1, TP.HCM");
        u1.setAvatarUrl("");
        
        User u2 = new User();
        u2.setId(2);
        u2.setEmail("khoa1@gmail.com");
        u2.setPassword("123456");
        u2.setFullName("Truong Khoa");
        u2.setPhone("0321645");
        u2.setAddress("456 Le Loi, Q3, TP.HCM");
        u2.setAvatarUrl("");
        
        list2.add(u1);
        list2.add(u2);
    }*/

    public global() {

    }

    public global(List<Comment> extList) {
        this.list3 = extList;
    }
    public List<Comment> getCmtData() {
        return this.list3;
    }

    public List<Product> getDefaultData() {
        return list;
    }

    public List<User> getUserData() {
        return  list2;
    }

    public User findUserById(long id) {
        for (User u : list2) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }
}
