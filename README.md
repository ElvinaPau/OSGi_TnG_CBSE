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

# Install Commands
karaf@root()> bundle:install file:///Users/<your-username>/Desktop/CBSE/OSGi_TnG_CBSE/ewallet/ewallet-commands/target/ewallet-commands-1.0-SNAPSHOT.jar
```

**Note:** Replace `<your-username>` with your actual username.

### 4. Start the Bundles

Note the bundle IDs from the installation step (e.g., 57, 58, 59, 60) and start them:

```bash
karaf@root()> bundle:start 57 58 59 60 61
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
- E-Wallet Commands

Verify services are registered:

```bash
karaf@root()> scr:list
```

## Usage

## Available Commands

The e-wallet provides the following Karaf shell commands:

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

### **Add Funds / Top-Up**

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

---

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
