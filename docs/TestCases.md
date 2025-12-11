# Smart Library Management System – Test Cases

This document contains functional test cases for the **Smart Library Management System (SLMS)** for ESU Kandy.

Each test case includes:

- **Test Case ID**
- **Function Tested**
- **Input / Steps**
- **Expected Output**

---

## 1. Authentication & User Management

### Table 1.1 – User Sign Up & Validation

| Test Case ID | Function Tested             | Input / Steps                                                                                                                                       | Expected Output                                                                                                    |
|--------------|-----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| TC001        | User Sign Up – valid data   | 1. From welcome screen, select `2. ➕ Sign Up`  2. Name: `Alex`  3. Email: `alex@esu.lk`  4. Contact: `0771234567`  5. Membership: `1` (Student)  6. Password: `1234` | System accepts all fields, creates new user, and prints: `✅ Sign up successful. Your User ID is: U10xx` and loads user main menu. |
| TC002        | User Sign Up – invalid email | 1. Select `2. ➕ Sign Up`  2. Name: `Alex`  3. Email: `123`  4. Contact: `0771234567`  5. Membership: `1` (Student)                                 | System rejects email and prints: `Please enter a valid email address (e.g. user@example.com).` and re-prompts for email. |
| TC003        | User Sign Up – invalid contact | 1. Select `2. ➕ Sign Up`  2. Name: `Alex`  3. Email: `alex@esu.lk`  4. Contact: `abc123`                                                           | System rejects contact and prints: `Contact number must be exactly 10 digits (numbers only).` and re-prompts.      |
| TC004        | User Sign Up – invalid name | 1. Select `2. ➕ Sign Up`  2. Name: `Alex123`  3. Email: `alex@esu.lk`  4. Contact: `0771234567`                                                    | System rejects name and prints: `Name should contain only letters and spaces.` and re-prompts for name.           |

### Table 1.2 – Login & Access Control

| Test Case ID | Function Tested              | Input / Steps                                                                                                                                                    | Expected Output                                                                                                         |
|--------------|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| TC005        | User Login – success         | 1. Select `1. 🔐 Login`  2. Enter valid `User ID = U1001`  3. Enter correct password                                                                            | System prints: `✅ Login successful. Welcome, <name>!` and shows **user main menu** (no admin-only options).            |
| TC006        | User Login – wrong password  | 1. Select `1. 🔐 Login`  2. `User ID = U1001`  3. Password = `wrong`                                                                                            | System prints: `❌ Invalid user ID or password.` and returns to welcome screen.                                         |
| TC007        | Admin Login – success        | 1. Select `3. 🔑 Admin Login`  2. Enter correct admin username & password (as defined in `LibrarianAuthService`)                                                | System prints: `✅ Admin login successful.` and shows **admin menu** with options 9 (Reports) and 10 (Administration).  |
| TC008        | Admin Login – invalid creds  | 1. Select `3. 🔑 Admin Login`  2. Enter wrong username and/or password                                                                                          | System prints: `❌ Invalid admin credentials.` and returns to welcome screen.                                           |

---

## 2. Book & User Management (Admin)

### Table 2.1 – Listing & Viewing

| Test Case ID | Function Tested           | Input / Steps                                               | Expected Output                                                                                               |
|--------------|---------------------------|-------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| TC009        | List Books – table view   | 1. Login (user or admin)  2. Select `1. 📖 List Books`      | System prints a formatted table: columns **ID, Title, Author, Category, ISBN, State** for all books.         |
| TC010        | List Users – table view   | 1. Login (user or admin)  2. Select `2. 👤 List Users`      | System prints a formatted table: columns **ID, Name, Email, Contact, Membership** for all users.             |

### Table 2.2 – Admin Book/User Management

| Test Case ID | Function Tested            | Input / Steps                                                                                                                                         | Expected Output                                                                                               |
|--------------|----------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| TC011A       | Add Book (admin)           | 1. Admin login  2. Select `10. 🔐 Administration` → `1. Manage Books` → `1. Add Book`  3. Enter valid Title, Author, Category, ISBN                   | System prints: `✅ Book added with ID: B10xx` and new book appears in **List Books**.                          |
| TC011B       | Add User (admin)           | 1. Admin login  2. `Administration` → `2. Manage Users` → `1. Add User`  3. Enter valid name, email, 10-digit contact, membership selection           | System prints: `✅ User added with ID: U10xx` and new user appears in **List Users**.                          |
| TC012A       | Update Book (admin)        | 1. Admin login  2. `Administration` → `1. Manage Books` → `2. Update Book`  3. Enter existing Book ID and new values (or blank to keep)               | System prints `✅ Book updated.` and updated data visible in **List Books**.                                   |
| TC012B       | Remove Book (admin)        | 1. Admin login  2. `Administration` → `1. Manage Books` → `3. Remove Book`  3. Enter an existing Book ID                                            | System prints `✅ Book removed (if it existed).` and book no longer appears in **List Books**.                |
| TC013A       | Remove User (admin)        | 1. Admin login  2. `Administration` → `2. Manage Users` → `2. Remove User`  3. Enter an existing User ID                                            | System prints `✅ User removed (if existed).` and user removed from **List Users** and observer list.         |

---

## 3. Borrowing, Returning & Fine Calculation

### Table 3.1 – Borrowing

| Test Case ID | Function Tested                  | Input / Steps                                                                                                                                                    | Expected Output                                                                                             |
|--------------|----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| TC014        | Borrow Book – valid              | 1. Login as normal user `U1001`  2. Select `3. ➕ Borrow Book`  3. Accept auto user ID or enter `U1001`  4. Enter `Book ID = B001` (Available)                   | System prints: `✅ Book borrowed. Due date: <date>` and creates new `BorrowTransaction`. Book state = Borrowed. |
| TC015        | Borrow Book – case-insensitive   | 1. Login as `U1001`  2. Select `3. ➕ Borrow Book`  3. Enter `userId = u1001`, `bookId = b001` in lowercase                                                      | System normalises IDs and still borrows the correct book, showing the same success message as TC014.        |
| TC016        | Borrow Book – exceeding limit    | 1. Ensure user `U1001` already has `borrowLimit` active loans (e.g. 5 for STUDENT)  2. Try to borrow one more book `B002`                                      | System prints: `⚠️ Borrow limit exceeded for user <name>` and does **not** create another transaction.      |
| TC017        | Borrow Book – book unavailable   | 1. Book `B003` is already borrowed by another user  2. Second user attempts to borrow `B003`                                                                    | System shows warning (from `BookState`) that the book cannot be borrowed in its current state.              |

### Table 3.2 – Returning & Fines

| Test Case ID | Function Tested                      | Input / Steps                                                                                                                                                  | Expected Output                                                                                                                  |
|--------------|--------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| TC018        | Return Book – on time (no fine)      | 1. Borrow book `B001` as `U1001`  2. Immediately select `4. 🔁 Return Book`  3. Enter `userId=U1001`, `bookId=B001`                                            | System prints: `✅ Book returned successfully.` and `finePaid = 0.0` in transaction. Book state back to Available/Reserved.      |
| TC019        | Return Book – overdue (Student)      | 1. Create a Student user loan with due date in the past (simulate overdue)  2. Select `4. 🔁 Return Book`                                                     | System prints success message plus `Fine: LKR <amount>` where amount = overdueDays × 50. Transaction `finePaid` updated.        |
| TC020        | Return Book – overdue (Faculty)      | 1. Create a Faculty user loan with overdue days  2. Return book via `4. 🔁 Return Book`                                                                       | System prints fine based on 20 LKR per day.                                                                                     |
| TC021        | Return Book – invalid user/book IDs  | 1. Select `4. 🔁 Return Book`  2. Enter non-existent `userId` or `bookId`                                                                                      | System prints: `❌ Invalid user or book ID.` and no changes made to transactions or book state.                                  |

---

## 4. Reservations & Notifications

### Table 4.1 – Reservations

| Test Case ID | Function Tested                   | Input / Steps                                                                                                                                     | Expected Output                                                                                                      |
|--------------|-----------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| TC022        | Reserve Book – valid              | 1. Ensure `B004` is currently borrowed  2. Login as `U1003`  3. Select `5. 📌 Reserve Book`  4. Enter `userId=U1003`, `bookId=B004`              | System prints: `✅ Book reserved successfully.` Reservation added in `LibraryService` and in book’s reservation list. |
| TC023        | Reserve Book – book available     | 1. Ensure `B005` is Available  2. Try to reserve via `5. 📌 Reserve Book`                                                                         | System prints: `⚠️ Book is available; you can borrow it instead of reserving.` and no reservation is created.       |
| TC024        | Cancel Reservation                | 1. Have an active reservation for `userId=U1003` and `bookId=B004`  2. Select `6. ❌ Cancel Reservation` and enter those IDs                      | System cancels reservation (marks inactive / removes) and prints confirmation message.                              |
| TC025        | Reserve Book – duplicate attempt  | 1. User `U1003` already has an active reservation for `B004`  2. Attempt to reserve `B004` again                                                  | System prevents duplicate or prints a suitable warning to the user.                                                 |

### Table 4.2 – Due / Overdue & Reservation Notifications

| Test Case ID | Function Tested             | Input / Steps                                                                                                                                                        | Expected Output                                                                                                      |
|--------------|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| TC026        | Notification – due soon     | 1. Create a borrow transaction where `dueDate = tomorrow`  2. Select `8. 🔔 Run Due/Overdue Notifications`                                                          | System prints: `Book '<title>' is due tomorrow (<date>).` for the corresponding user.                               |
| TC027        | Notification – overdue      | 1. Create a borrow transaction where `dueDate` is in the past (overdue)  2. Select `8. 🔔 Run Due/Overdue Notifications`                                            | System prints: `Book '<title>' is overdue by N days.` for that user.                                                |
| TC028        | Notification – reservation available | 1. Have a reserved book `B006`  2. When the current borrower returns `B006`, system processes reservation queue in `returnBook` logic                               | System sends a notification event to the first reserving user: e.g. “Book '<title>' is now available for you.”      |

---

## 5. Command Pattern & Undo

### Table 5.1 – Undo Operations

| Test Case ID | Function Tested                | Input / Steps                                                                                                                                          | Expected Output                                                                                                                 |
|--------------|--------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| TC029        | Undo Last Action – borrow      | 1. Borrow a book using `3. ➕ Borrow Book`  2. Select `7. ↩️ Undo Last Action`                                                                          | System calls `undo()` on the last `BorrowBookCommand`, effectively returning the book with `forceReturnWithoutFine`. Book no longer shows as borrowed. |
| TC030        | Undo Last Action – return      | 1. Borrow a book  2. Return it using `4. 🔁 Return Book`  3. Select `7. ↩️ Undo Last Action`                                                           | System calls `undo()` on `ReturnBookCommand`, making the book borrowed again in the system and reverting return metadata.      |
| TC031        | Undo Last Action – nothing to undo | 1. Start app, go to main menu  2. Select `7. ↩️ Undo Last Action` without executing any commands before                                               | System prints warning such as: `⚠️ Nothing to undo.`                                                                          |

---

## 6. Reports & Borrow History

### Table 6.1 – History

| Test Case ID | Function Tested                      | Input / Steps                                                                                                                                              | Expected Output                                                                                             |
|--------------|--------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| TC032        | View Borrow History – by User        | 1. Ensure user `U1001` has multiple transactions  2. Select `11. 📜 View Borrow History` → `1. View by User ID`  3. Enter `U1001`                         | System prints formatted history table: TxID, BookID, Title, Borrow, Due, Return, Fine for user `U1001`.     |
| TC033        | View Borrow History – by Book        | 1. Ensure book `B001` has been borrowed by several users  2. Select `11` → `2. View by Book ID`  3. Enter `B001`                                           | System prints table: TxID, UserID, UserName, Borrow, Due, Return, Fine for book `B001`.                     |

### Table 6.2 – Reports (Admin)

| Test Case ID | Function Tested             | Input / Steps                                                                                                     | Expected Output                                                                                  |
|--------------|-----------------------------|-------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| TC034        | Most Borrowed Books         | 1. Admin login  2. Select `9. 📊 View Reports` → `1. Most Borrowed Books`                                         | System prints top N most borrowed books, with counts, based on data from `LibraryService`.      |
| TC035        | Active Borrowers            | 1. Admin login  2. `9. 📊 View Reports` → `2. Active Borrowers`                                                   | System lists users who currently have unreturned books, based on active transactions.           |
| TC036        | Overdue Books Report        | 1. Admin login  2. `9. 📊 View Reports` → `3. Overdue Books`                                                      | System prints all overdue transactions with user, book, and overdue days.                       |

---

## 7. Usability & Input Handling

### Table 7.1 – Usability Behaviour

| Test Case ID | Function Tested                      | Input / Steps                                                                                                                       | Expected Output                                                                                          |
|--------------|--------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| TC037        | Case-insensitive User/Book IDs       | 1. At any borrow/return/reserve/cancel/history prompt, enter `u1001` instead of `U1001`, or `b001` instead of `B001`               | System normalises IDs to upper case and correctly finds user/book without error.                        |
| TC038        | `#` shortcut – sign up / menus       | 1. Start sign-up, when asked for name/email/contact, enter `#`  2. Or in any sub-menu prompt, enter `#`                            | System cancels the current operation and returns to the previous/main menu without crashing.            |
| TC039        | Input validation – membership type   | 1. During sign up or admin add user, enter invalid membership option (e.g. `4`) when prompted `Membership Type (1/2/3)`            | System prints an error message and re-prompts for a valid membership type.                             |
| TC040        | Exit system                          | 1. From main menu, select `0. 🚪 Exit`                                                                                              | System prints goodbye message (e.g. “👋 Exiting Smart Library Management System. Bye!”) and terminates. |

---

### Notes for Execution

- For overdue and notification tests, you may need to:
  - Temporarily adjust dates in `BorrowTransaction`, or  
  - Seed specific data in `LibraryService.seedSampleData()` for testing.

```markdown
