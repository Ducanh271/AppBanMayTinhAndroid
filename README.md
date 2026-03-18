# App Bán Máy Tính (Android - Jetpack Compose)

Ứng dụng Android bán sản phẩm (máy tính/linh kiện) viết bằng **Kotlin + Jetpack Compose**, có các chức năng:
- Đăng ký / Đăng nhập
- Xem danh sách sản phẩm + phân trang + tìm kiếm + lọc theo danh mục
- Xem chi tiết sản phẩm
- Thêm vào giỏ hàng, cập nhật số lượng, xoá sản phẩm, tính tổng tiền
- Checkout:
  - Mua ngay 1 sản phẩm
  - Thanh toán từ giỏ hàng (nhiều sản phẩm)
- Quản lý đơn hàng: danh sách đơn, xem chi tiết đơn
- Quản lý tài khoản: xem/cập nhật thông tin
- Tích hợp **ZaloPay SDK (Sandbox)** và deep link callback `demozpdk://app`
- Có màn hình Chat (demo)

---

## 1) Demo luồng sử dụng (User Flow)

### 1.1 Đăng ký / Đăng nhập
- **Đăng ký (Sign Up)**: tạo tài khoản mới
  - Sau khi đăng ký, app **tự tạo giỏ hàng** cho user (gọi API cart).
- **Đăng nhập (Login)**:
  - Lưu `userId`, `userName`, `userEmail` vào SharedPreferences.
  - App theo dõi trạng thái đăng nhập để điều hướng tự động.

**Điều hướng:**
- Nếu đã đăng nhập → vào danh sách sản phẩm
- Nếu chưa đăng nhập → vào màn Login

---

### 1.2 Sản phẩm
Tính năng phía sản phẩm (Product):
- Xem **danh sách sản phẩm** (`product_list`)
- **Phân trang** (load next page)
- **Tìm kiếm** theo từ khoá
- Lấy danh sách **categories** và lọc theo category
- Xem **chi tiết sản phẩm** (`product_detail/{productId}`)

Từ màn chi tiết có thể:
- **Mua ngay** → đi Checkout theo sản phẩm
- **Thêm vào giỏ hàng**

---

### 1.3 Giỏ hàng (Cart)
Màn `cart` cho phép:
- Tải giỏ hàng theo `userId`
- Thêm sản phẩm vào giỏ
- Cập nhật số lượng (có cập nhật cục bộ để UI phản hồi nhanh)
- Xoá sản phẩm khỏi giỏ
- Clear giỏ
- Tính tổng tiền

Từ giỏ hàng:
- Bấm Checkout → `checkout_cart`

---

### 1.4 Checkout / Đặt hàng (Orders)
Có 2 dạng checkout:
1) **Checkout mua ngay 1 sản phẩm**: `checkout/{productId}`
2) **Checkout từ giỏ hàng**: `checkout_cart`

Tạo đơn hàng có các thông tin:
- `userId`
- danh sách items `{productId, quantity}`
- `address`, `phoneNumber`

Thanh toán/đặt hàng hiện thấy hỗ trợ:
- **COD (Cash On Delivery)** cho:
  - 1 sản phẩm
  - nhiều sản phẩm từ giỏ hàng

Sau khi đặt hàng thành công:
- App có thể điều hướng sang **chi tiết đơn hàng** (`order_detail/{orderId}`) tuỳ flow.

---

### 1.5 Quản lý đơn hàng
- Danh sách đơn hàng: `orders`
- Xem chi tiết đơn: `order_detail/{orderId}`

---

### 1.6 Tài khoản
- Xem tài khoản: `account`
- Chỉnh sửa tài khoản: `edit_account` (cập nhật tên/email và lưu lại vào SharedPreferences)

---


## 2) Danh sách màn hình / Routes

Các route chính được khai báo trong `AppNavigation.kt`:

- `login`
- `sign_up`
- `product_list` (màn chính / danh sách sản phẩm)
- `home` (được dùng như màn quay về sau khi thanh toán)
- `product_detail/{productId}`
- `cart`
- `checkout_cart`
- `checkout/{productId}`
- `orders`
- `order_detail/{orderId}`
- `account`
- `edit_account`
- `chat`

---

## 3) Công nghệ sử dụng

- Kotlin, Coroutines
- Jetpack Compose + Material 3
- Navigation Compose
- Retrofit2 + Gson
- OkHttp
- Coil (load ảnh)
- AndroidX Security Crypto
- ZaloPay SDK (Sandbox)

---

## 4) Cấu hình dự án

- Module chính: `:app`
- `minSdk`: 24
- `targetSdk`: 34
- `compileSdk`: 35
- Java/Kotlin target: 17
- Compose enabled

---

## 5) Tích hợp ZaloPay (Sandbox) + Deep link callback

### 5.1 Init SDK
Trong `MainActivity`:
- App init ZaloPay SDK với **Environment.SANDBOX**
- Có xử lý callback qua `onNewIntent()`

### 5.2 Deep link
Trong `AndroidManifest.xml`:
- Scheme: `demozpdk`
- Host: `app`

Ví dụ deep link:
- `demozpdk://app`

> Khi chạy test thanh toán, cần đảm bảo intent-filter đúng và thiết bị có thể nhận callback.

---

## 6) Cách chạy project

### Android Studio
1. Mở project bằng Android Studio
2. Sync Gradle
3. Run module `app`

### Command line
```bash
# Windows
.\gradlew.bat assembleDebug

# macOS/Linux
./gradlew assembleDebug
```
