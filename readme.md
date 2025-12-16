# All-In: Real-Time Stock Market Simulation

> *"Trade big. Win big. Risk nothing."*

**Your trading instincts are getting rusty.** The real market is too slow, too risky, or just plain boring. You need a place to sharpen your edge without losing your shirt.

**Introducing All_In.**

This is your personal sandbox for market domination. Go "all in" on a real-time stock simulation featuring the hottest imaginary tickers like **Ramen Inc** and **SomeTech**.

* **Test your gut:** Read the charts and make the call.
* **Max your profits:** See how high you can push your portfolio.
* **Zero consequences:** The thrill of the gamble with none of the regret.

**Stop watching the ticker. Start moving the market.**

## Quick Start

Follow these steps to set up the directory and run the game:

1.  **Clone the Repository:**
    Run this command to download the code **into the current directory**:
    ```bash
    git clone https://github.com/Clue0110/All_In.git .
    ```
    *(Note the `.` at the end of the command. This ensures the files are placed directly inside your new `All_In` folder).*


2.  **Enter the Directory:**
    Open your terminal and run:
    ```bash
    cd All_In
    ```

3.  **Make the Script Executable:**
    ```bash
    chmod +x setup.sh
    ```

4.  **Run the Script:**
    ```bash
    ./setup.sh
    ```

## How to Play

Once the script finishes and the server is running, open your browser to:

* **http://localhost:8080** (Standard web port)

*(Check your terminal output for the exact address if these do not work.)*


## Troubleshooting

### Port Conflicts
If you see `Bind for 0.0.0.0:8080 failed`:
* Another application is using port 8080.
* **Fix:** Edit `setup.sh` and change the port mapping (e.g., `-p 8081:80`).

### "Command not found"
If the script fails to run:
* Ensure you are in the correct directory using `pwd`.
* Ensure you ran `chmod +x setup.sh`.