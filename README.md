#📱 Lumen – Expense Tracker
Lumen is a lightweight and intuitive Android application designed to help users track and manage their expenses. It was developed as my final project for Harvard’s CS50 course, using modern Android development practices and technologies such as Kotlin, Jetpack Compose, and Firebase.

### 🎥 Video Demo: https://youtu.be/DeaXhz4ox-g

### Description:
🛠️ Technologies Used
Kotlin: Modern, concise language for Android development.

Jetpack Compose: Declarative UI toolkit from Google.

MVVM Architecture: Clean separation of UI and business logic.

Jetpack Navigation: For seamless screen transitions.

Firebase Authentication: Enables login/signup via email and Google.

Firebase Firestore: Real-time NoSQL database for syncing data online.

Room Database: Offline storage with full transaction history persistence.

Multilanguage Support: English, Portuguese, and Spanish.

Multicurrency Support: Supports different currency symbols based on locale.

Dark Mode: Adapts to system theme.

🌟 Key Features
🔄 Online and Offline Support
Lumen was built with both online and offline usability in mind. It leverages Firebase for authentication and cloud storage but also includes a local Room database, so users can continue tracking their transactions even when offline. Data is synchronized automatically when connectivity is restored.

🔐 Sign Up and Sign In
Users can create an account or log in using their email and password via Firebase Authentication. There’s also a convenient option to use Google Sign-In. After successful authentication, users are redirected to the Home Screen.

🎬 Splash Screen
The app starts with a smooth animated splash screen, creating a pleasant first impression and branding touch.

🏠 Home Screen
The main screen features a TabView at the bottom, managed with Jetpack Navigation and a horizontal pager. The primary tabs are:

📊 Finance Tracker Tab
This tab displays a summary of the user’s financial activity, including:

A pie chart representing the distribution of expenses or incomes by category or transaction type (e.g., bills, credit card).

A visual breakdown that helps users see where their money goes.

A summary row showing total income, total expenses, and current balance.

A list of categories with their respective totals.

Filters at the top for switching between transaction types and selecting custom time periods (e.g., current month, last three months).

📁 Transactions Tab
A complete list of all transactions added by the user. Key features include:

A search field to look up transactions by title or description.

A filter icon leading to an advanced filter screen (by date, category, amount, etc.).

Each transaction card provides buttons to edit or delete the transaction.

➕ Add Transaction
Users can add new transactions using a simple, clean form that collects:

Title

Amount

Date

Description

Transaction Type (Expense or Income)

Category (e.g., Bills, Food, Credit Card)

This screen is also reused when editing an existing transaction.

⚙️ User Configuration
The configuration screen allows users to:

Upload or change their profile photo and name.

Set their preferred language and currency.

Log in or log out, depending on the current authentication state.

Delete account data if needed.

Settings like name, language, and currency are updated using bottom sheets, which provide a smooth and familiar Android UX.

🌐 Multilanguage and Multicurrency
Lumen supports three languages—English, Portuguese, and Spanish—making it accessible to a wider audience. Additionally, users can choose their preferred currency, and the app will display all values formatted accordingly.

🌙 Dark Mode
The entire app is designed to work seamlessly with both light and dark themes, improving usability and reducing eye strain in low-light environments.
