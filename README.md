# OSGi_TnG_CBSE

A modular e-wallet application built using OSGi and Apache Karaf, demonstrating component-based software engineering principles.

## Project Structure

```
ewallet/
├── ewallet-api/              # Entities and Service interfaces
├── user-component/           # User service implementation
├── wallet-component/         # Wallet service implementation
└── ewallet-commands/         # Karaf shell commands
```

## Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
- **Apache Maven**: Version 3.6 or higher
- **Apache Karaf**: Version 4.4.9
- **Git**: For version control

## Setup Instructions

### 1. Install Apache Karaf

Download and extract Apache Karaf 4.4.9:

```bash
# Download Karaf (or download manually from https://karaf.apache.org/download.html)
cd ~/Desktop/CBSE
wget https://archive.apache.org/dist/karaf/4.4.9/apache-karaf-4.4.9.tar.gz
tar -xzf apache-karaf-4.4.9.tar.gz
```

### 2. Build the Project

Build all modules using Maven:

```bash
cd ewallet
mvn clean install
```

This will compile all bundles and create JAR files in each module's `target/` directory.

## Deployment to Karaf

### 1. Start Karaf with Clean Cache

```bash
cd ~/Desktop/CBSE/apache-karaf-4.4.9/bin
./karaf clean
```

### 2. Install Required Features

Once Karaf starts, install the Service Component Runtime (SCR):

```bash
karaf@root()> feature:install scr
```

Verify installation:

```bash
karaf@root()> feature:list | grep scr
```

### 3. Install E-Wallet Bundles

Install the bundles in the following order:

```bash
# Install API bundle
karaf@root()> bundle:install file:///Users/<your-username>/Desktop/CBSE/OSGi_TnG_CBSE/ewallet/ewallet-api/target/ewallet-api-1.0-SNAPSHOT.jar

# Install User Component
karaf@root()> bundle:install file:///Users/<your-username>/Desktop/CBSE/OSGi_TnG_CBSE/ewallet/user-component/target/user-component-1.0-SNAPSHOT.jar

# Install Wallet Component
karaf@root()> bundle:install file:///Users/<your-username>/Desktop/CBSE/OSGi_TnG_CBSE/ewallet/wallet-component/target/wallet-component-1.0-SNAPSHOT.jar

# Install Payment Component
karaf@root()> bundle:install -s file:///Users/<your-username>/Document/GitHub/OSGi_TnG_CBSE/ewallet/payment-component/target/payment-component-1.0-SNAPSHOT.jar

# Install Investment Component
karaf@root()> bundle:install -s file:///Users/<your-username>/Document/Github/OSGi_TnG_CBSE/ewallet/investment-component/target/investment-component-1.0-SNAPSHOT.jar

# Install Commands
karaf@root()> bundle:install file:///Users/<your-username>/Desktop/CBSE/OSGi_TnG_CBSE/ewallet/ewallet-commands/target/ewallet-commands-1.0-SNAPSHOT.jar
```

**Note:** Replace `<your-username>` with your actual username.

### 4. Start the Bundles

Note the bundle IDs from the installation step (e.g., 57, 58, 59, 60) and start them:

```bash
karaf@root()> bundle:start 57 58 59 60 61 62
```

### 5. Verify Installation

Check that all bundles are active:

```bash
karaf@root()> bundle:list
```

You should see all four bundles in **Active** state:

- E-Wallet API
- User Component
- Wallet Component
- Payment Component
- Investment Component
- E-Wallet Commands

Verify services are registered:

```bash
karaf@root()> scr:list
```

## Usage

## Available Commands (Wallet Module)

### **Create a User**

```bash
karaf@root()> ewallet:create-user <phoneNumber> <username>
```

**Example:**

```bash
karaf@root()> ewallet:create-user 0123456789 Alice
User created successfully!
  Phone Number: 0123456789
  Username: Alice
  User ID: 123e4567-e89b-12d3-a456-426614174000
```

---

### **Create a Wallet**

```bash
karaf@root()> ewallet:create-wallet <phoneNumber> <username> <initial-balance>
```

**Example:**

```bash
karaf@root()> ewallet:create-wallet 0123456789 Alice 500.00
Wallet created successfully!
  Phone Number: 0123456789
  Username: Alice
  Initial Balance: RM 500.00
```

---

### **Check Wallet Balance**

```bash
karaf@root()> ewallet:balance <phoneNumber>
```

**Example:**

```bash
karaf@root()> ewallet:balance 0123456789
=== Wallet Balance ===
  Phone Number: 0123456789
  Username: Alice
  Balance: RM 500.00
```

---

### **Add Money**

```bash
karaf@root()> ewallet:add-money <phoneNumber> <amount>
```

**Example:**

```bash
karaf@root()> ewallet:add-money 0123456789 100
Money added successfully!
  Amount Added: RM 100.00
  Old Balance: RM 500.00
  New Balance: RM 600.00
```

---

### **Deduct Balance**

```bash
karaf@root()> ewallet:deduct <phoneNumber> <amount> "Description"
```

**Example:**

```bash
karaf@root()> ewallet:deduct 0123456789 50 "Coffee payment"
Balance deducted successfully!
  Amount Deducted: RM 50.00
  Description: Coffee payment
  Old Balance: RM 600.00
  New Balance: RM 550.00
```

---

### **Send Money to Another User**

```bash
karaf@root()> ewallet:send-money <senderPhone> <recipientPhone> <amount>
```

**Example:**

```bash
karaf@root()> ewallet:send-money 0123456789 0987654321 150
Money sent successfully!
  Amount: RM 150.00
  Sender Old Balance: RM 550.00
  Sender New Balance: RM 400.00
```

---

### **View Wallet Transaction History**

```bash
karaf@root()> ewallet:wallet-history <phoneNumber>
```

**Example:**

```bash
karaf@root()> ewallet:wallet-history 0123456789
=== Transaction History ===
Phone Number: 0123456789
Username: Alice
Current Balance: RM 400.00
-------------------------------
[2026-01-14T20:10:00] TOP_UP: RM 100.00 - Added money
[2026-01-14T20:15:00] SEND: RM 150.00 - Sent to Bob (0987654321)
[2026-01-14T20:20:00] RECEIVE: RM 50.00 - Received from Charlie (0111222333)
```
### **Full List of Commands**

| Command                  | Description                                | Usage Example                                      |
| ------------------------ | ------------------------------------------ | -------------------------------------------------- |
| `ewallet:create-user`    | Create a new user                          | `ewallet:create-user 0123456789 Alice`             |
| `ewallet:create-wallet`  | Create a wallet with initial balance       | `ewallet:create-wallet 0123456789 Alice 500`       |
| `ewallet:balance`        | Check wallet balance                       | `ewallet:balance 0123456789`                       |
| `ewallet:add-money`      | Add funds to wallet                        | `ewallet:add-money 0123456789 100`                 |
| `ewallet:deduct`         | Deduct balance from wallet                 | `ewallet:deduct 0123456789 50 "Coffee payment"` |
| `ewallet:send-money`     | Send money to another user by phone number | `ewallet:send-money 0123456789 0987654321 150`     |
| `ewallet:wallet-history` | View wallet transaction history            | `ewallet:wallet-history 0123456789`                |

---

## Available Commands (Payment Module)

### **Make a Payment to a Merchant**

```bash
karaf@root()> ewallet:pay <phoneNumber> <merchant> <amount>
```

**Example:**

```bash
karaf@root()> ewallet:pay 0123456789 Starbucks 25.50
Payment Successful!
Elvina (0123456789) paid RM 25.50 to Starbucks
```

---

### **Top-Up Wallet Funds**

```bash
karaf@root()> ewallet:topup <phoneNumber> <amount>
```

**Example:**

```bash
karaf@root()> ewallet:topup 0123456789 100
Top-Up Successful!
Added RM 100.00 to Elvina's wallet (0123456789)
```

---

### **Scan and Pay via QR String**

```bash
karaf@root()> ewallet:scan-qr <phoneNumber> "<merchant:amount>"
```

**Example:**

```bash
karaf@root()> ewallet:scan-qr 0123456789 "Starbucks:15.50"
QR Payment Successful! Elvina (0123456789) paid RM 15.50 to Starbucks
```

---

### **Manage AutoPay Settings**

```bash
karaf@root()> ewallet:autopay <phoneNumber> <action> [biller] [amount]
```

- **setup** → register a new AutoPay  
- **run** → simulate AutoPay execution  

**Examples:**

```bash
karaf@root()> ewallet:autopay 0123456789 setup TNB 80
AutoPay registered for Elvina (0123456789): TNB (RM 80.00)

karaf@root()> ewallet:autopay 0123456789 run
Simulating AutoPay execution for Elvina (0123456789)...
Processed AutoPay: TNB RM 80.00
```

---

### **View Payment History**

```bash
karaf@root()> ewallet:history <phoneNumber> [type]
```

- `general` → retail/top-up payments (default)  
- `qr` → QR payments  
- `autopay` → AutoPay logs  

**Examples:**

```bash
karaf@root()> ewallet:history 0123456789
=== GENERAL HISTORY for Elvina (0123456789) ===
[2026-01-15T13:30:00] TOPUP: RM 100.00 | Wallet Top-Up | SUCCESS
[2026-01-15T13:35:00] RETAIL: RM 25.50 | Starbucks | SUCCESS

karaf@root()> ewallet:history 0123456789 qr
=== QR HISTORY for Elvina (0123456789) ===
[2026-01-15T13:40:00] QR: RM 15.50 | Starbucks | SUCCESS

karaf@root()> ewallet:history 0123456789 autopay
=== AUTOPAY HISTORY for Elvina (0123456789) ===
[2026-01-15T13:45:00] AUTOPAY: RM 80.00 | TNB | SUCCESS
```

### **Full List of Payment Commands**

| Command              | Description                          | Usage Example                                    |
| -------------------- | ------------------------------------ | ------------------------------------------------ |
| `ewallet:pay`        | Make a payment to a merchant         | `ewallet:pay 0123456789 Starbucks 25.50`         |
| `ewallet:topup`      | Top-up wallet funds                  | `ewallet:topup 0123456789 100`                   |
| `ewallet:scan-qr`    | Scan and pay via QR string           | `ewallet:scan-qr 0123456789 "Starbucks:15.50"`   |
| `ewallet:autopay`    | Manage AutoPay (setup/run)           | `ewallet:autopay 0123456789 setup TNB 80`        |
| `ewallet:history`    | View payment history (general/qr/autopay) | `ewallet:history 0123456789 qr`             |

---

## Available Commands (Investment Module)

### **Show Available Investment Funds**

```bash
karaf@root()> ewallet:invest-list <username>
```

- username is optional

**Example:**

```bash
karaf@root()> ewallet:invest-list

#   | ID     | NAME                      |   NAV (RM) | RISK     |      OWNED
-----------------------------------------------------------------------------
1   | F01    | Low Risk Income Fund      |     1.0000 | Low      |     0.0000
2   | F02    | Balanced Global Fund      |     2.5000 | Medium   |     0.0000
3   | F03    | Equity Growth Fund        |     5.7500 | High     |     0.0000
4   | F04    | Digital Assets Fund       |    10.2000 | High     |     0.0000

karaf@root()> ewallet:invest-list Alice
Username: Alice

#   | ID     | NAME                      |   NAV (RM) | RISK     |      OWNED
-----------------------------------------------------------------------------
1   | F01    | Low Risk Income Fund      |     1.0000 | Low      |     0.0000
2   | F02    | Balanced Global Fund      |     2.5000 | Medium   |     0.0000
3   | F03    | Equity Growth Fund        |     5.7500 | High     |     0.0000
4   | F04    | Digital Assets Fund       |    10.2000 | High     |     0.0000
```

---

### **Buy or Sell Fund Units**

```bash
karaf@root()> ewallet:invest-trade <phoneNumber> <username> <action> <fundId> <amount>
```

**Example:**
```bash
karaf@root()> ewallet:invest-trade 0123456789 Alice BUY F02 50
Successfully invested RM 50.0 in F02

karaf@root()> ewallet:invest-trade 0123456789 Alice SELL F02 20
Successfully sold 20.0 units of F02
```

---

### **Show Investment History**

```bash
karaf@root()> ewallet:invest-history <username>
```

**Example:**
```bash
karaf@root()> ewallet:invest-history Alice

===============================================================================================
                  INVESTMENT TRANSACTION HISTORY: ALICE
===============================================================================================
TYPE       | FUND ID      | STATUS   | AMOUNT       | UNITS      | DATE
-----------------------------------------------------------------------------------------------
BUY        | F02          | SUCCESS  | - RM 50.00   | 20.0000    | Thu Jan 15 23:55:22 MYT 2026
SELL       | F02          | SUCCESS  | + RM 50.00   | 20.0000    | Thu Jan 15 23:56:44 MYT 2026
===============================================================================================
Total Transactions: 2
===============================================================================================

```

---

### **View User Portfolio and Returns**

```bash
karaf@root()> ewallet:invest-portfolio <username>
```

**Example:**
```bash
karaf@root()> ewallet:invest-portfolio Alice

==========================================
         YOUR PORTFOLIO SUMMARY
==========================================
Username     : Alice
Risk Profile : Not Assessed (Take the quiz!)
------------------------------------------
CURRENT HOLDINGS:
------------------------------------------
Net Performance : RM 0.00
Overall Status  : PROFIT
==========================================
```

---

### **Take Risk Assessment Quiz**

```bash
karaf@root()> ewallet:risk-quiz <username>
```
- User input will not be shown in the console
- After typing a number (1-3), click 'Enter' 2 times to go to next question

**Example:**
```bash
karaf@root()> ewallet:risk-quiz Alice
RISK ASSESSMENT QUIZ
Hello Alice, let's determine your risk profile.

1. What is your investment goal?
   (1) Preserve Capital
   (2) Balanced Growth
   (3) Maximize Returns
Your choice (1-3): 
2. How do you react if your investment drops 10%?
   (1) Sell everything
   (2) Do nothing
   (3) Buy more
Your choice (1-3):
3. What is your investment timeframe?
   (1) < 1 year
   (2) 1-5 years
   (3) 5+ years
Your choice (1-3):
----------------------------------------------------
RESULT: Your Risk Profile is MODERATE
----------------------------------------------------
Based on your profile, we recommend looking at:
 >> Balanced Global Fund (ID: F02)
```

---

### **Simulate Market Price Changes**

```bash
karaf@root()> ewallet:invest-simulate
```

**Example:**
```bash
karaf@root()> ewallet:invest-simulate
[Market] Simulating fund price changes based on volatility...
[Market] Low Risk Income Fund     : RM   1.0000 -> RM   1.0095 (+0.95%)
[Market] Balanced Global Fund     : RM   2.5000 -> RM   2.4012 (-3.95%)
[Market] Equity Growth Fund       : RM   5.7500 -> RM   5.7229 (-0.47%)
[Market] Digital Assets Fund      : RM  10.2000 -> RM  10.4999 (+2.94%)
```

### **Full List of Investment Commands**

| Command                    | Description                       | Usage Example                                    |
| -------------------------- | --------------------------------- | ------------------------------------------------ |
| `ewallet:invest-list`      | Show available investment funds   | `ewallet:invest-list` OR </br>`ewallet:invest-list Alice`  |
| `ewallet:invest-trade`     | Buy or Sell fund units            | `ewallet:invest-trade 0123456789 Alice BUY F02 50` <br/> `ewallet:invest-trade 0123456789 Alice SELL F02 20` |
| `ewallet:invest-history`   | Show investment history           | `ewallet:invest-history Alice`        |
| `ewallet:invest-portfolio` | View user portfolio and returns   | `ewallet:invest-portfolio Alice`      |
| `ewallet:risk-quiz`        | Take Risk Assessment Quiz         | `ewallet:risk-quiz Alice`             |
| `ewallet:invest-simulate`  | Simulate market price changes     | `ewallet:invest-simulate`             |

---

## Troubleshooting

### Bundle Won't Start

If a bundle fails to start, diagnose the issue:

```bash
karaf@root()> bundle:diag <bundle-id>
```

### Command Not Found

If commands aren't recognized:

1. Verify the bundle is active:

   ```bash
   karaf@root()> bundle:list
   ```

2. Check if commands are registered:

   ```bash
   karaf@root()> help | grep ewallet
   ```

3. Restart the commands bundle:
   ```bash
   karaf@root()> bundle:restart <commands-bundle-id>
   ```

### Service Not Available

If you get "Service not available" errors:

1. Check service registration:

   ```bash
   karaf@root()> scr:list
   ```

2. View service details:
   ```bash
   karaf@root()> scr:info com.tng.user.impl.UserServiceImpl
   karaf@root()> scr:info com.tng.wallet.impl.WalletServiceImpl
   ```

### Rebuilding After Code Changes

After making code changes:

1. Rebuild the affected module:

   ```bash
   cd ~/Desktop/CBSE/OSGi_TnG_CBSE/ewallet/<module-name>
   mvn clean install
   ```

2. Update the bundle in Karaf:
   ```bash
   karaf@root()> bundle:update <bundle-id>
   ```

### Clean Restart

If you encounter persistent issues, restart Karaf with a clean cache:

```bash
# Exit Karaf (Ctrl+D or type 'logout')
cd ~/Desktop/CBSE/apache-karaf-4.4.9/bin
./karaf clean
```

Then reinstall all bundles following the deployment steps.

## Development Guidelines

### Adding New Commands

1. Create a new command class in `ewallet-commands/src/main/java/com/tng/commands/`
2. Annotate with `@Command` and `@Service`
3. Implement the `Action` interface
4. Use `@Reference` for service dependencies
5. Rebuild and update the bundle

Example:

```java
@Command(scope = "ewallet", name = "my-command", description = "Description")
@Service
public class MyCommand implements Action {

    @Reference
    private UserService userService;

    @Override
    public Object execute() throws Exception {
        // Command logic here
        return null;
    }
}
```

### Adding New Services

1. Define the interface in `ewallet-api`
2. Create implementation in a new component module
3. Annotate implementation with `@Component(service = YourService.class)`
4. Update POMs with correct dependencies and OSGi configuration

## Project Architecture

### Module Dependencies

```
ewallet-commands
    ↓ (depends on)
ewallet-api
    ↑ (implemented by)
user-component, wallet-component
```

### Key Technologies

- **OSGi**: Modular runtime platform
- **Apache Karaf**: OSGi container with shell
- **Declarative Services (DS)**: Component lifecycle management
- **Maven Bundle Plugin**: OSGi bundle creation

## Important POM Configuration Notes

### For Service Components (user-component, wallet-component)

- Use `<packaging>bundle</packaging>`
- Include maven-bundle-plugin with `<_dsannotations>*</_dsannotations>`
- Do NOT import `org.osgi.service.component.annotations` at runtime
- Use `<scope>provided</scope>` for OSGi dependencies

### For Commands (ewallet-commands)

- Use `<packaging>bundle</packaging>`
- Export command package: `<Export-Package>com.tng.commands</Export-Package>`
- Include: `<Karaf-Commands>com.tng.commands</Karaf-Commands>`
- Add Karaf shell dependencies with `<scope>provided</scope>`

## Team Collaboration Tips

1. **Always rebuild after pulling changes:**

   ```bash
   git pull
   mvn clean install
   ```

2. **Use consistent Java paths:** Update bundle install paths in your local environment

3. **Check Karaf logs:** Use `log:tail` or `log:display` to debug issues

4. **Share bundle IDs:** Document which bundle ID corresponds to which module

5. **Version control:** Commit working code; test in Karaf before pushing

## Useful Karaf Commands Reference

| Command                | Description                       |
| ---------------------- | --------------------------------- |
| `bundle:list`          | List all installed bundles        |
| `bundle:install <url>` | Install a bundle                  |
| `bundle:start <id>`    | Start a bundle                    |
| `bundle:stop <id>`     | Stop a bundle                     |
| `bundle:restart <id>`  | Restart a bundle                  |
| `bundle:update <id>`   | Update a bundle                   |
| `bundle:headers <id>`  | Show bundle manifest headers      |
| `bundle:diag <id>`     | Diagnose bundle issues            |
| `scr:list`             | List all DS components            |
| `scr:info <component>` | Show component details            |
| `feature:install scr`  | Install Service Component Runtime |
| `log:tail`             | Tail the log output               |
| `log:display`          | Display log entries               |
| `help`                 | Show all available commands       |
