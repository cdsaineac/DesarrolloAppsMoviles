package co.edu.unal.tictactoe;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class TicTacToeActivity extends Activity {
    // Represents the internal state of the game
    private TicTacToeGame mGame;
    // Buttons making up the board
    private Button[] mBoardButtons;
    // Menu Buttons
    private Button mNewGameButton;
    private Button mRestartStatsButton;
    // Various text displayed
    private TextView mInfoTextView;
    private TextView mHumanVictories;
    private TextView mAndroidVictories;
    private TextView mTies;
    // Variables to count results
    private int humanVictories = 0;
    private int androidVictories = 0;
    private int ties = 0;
    // Represents status of the Game
    public boolean mGameOver;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_QUIT_ID = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                           getResources().getString(R.string.difficulty_expert)};
                // TODO: Set selected, an integer (0 to n-1), for the Difficulty dialog.
                // selected is the radio button that should be selected.
                int selected = DIALOG_DIFFICULTY_ID;
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                // TODO: Set the diff level of mGame based on which item was selected.
                                switch (item){
                                    case 0:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                        startNewGame();
                                        break;
                                    case 1:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                        startNewGame();
                                        break;
                                    case 2:
                                        mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                        startNewGame();
                                        break;
                                }
                                // Display the selected difficulty level
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                TicTacToeActivity.this.finish();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setActionBar(myToolbar);
        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanVictories = (TextView) findViewById(R.id.Human_Victories);
        mHumanVictories.setText("0");
        mAndroidVictories = (TextView) findViewById(R.id.Android_Victories);
        mAndroidVictories.setText("0");
        mTies = (TextView) findViewById(R.id.Ties);
        mTies.setText("0");
        //mNewGameButton= (Button) findViewById(R.id.NewGame);
        //mRestartStatsButton = (Button) findViewById(R.id.RestartStats);
        mGame = new TicTacToeGame();
        startNewGame();
    }


    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        mGameOver = false;
        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        //mNewGameButton.setEnabled(true);
        //mNewGameButton.setOnClickListener(new ButtonClickListener(9));
        //mRestartStatsButton.setEnabled(true);
        //mRestartStatsButton.setOnClickListener(new ButtonClickListener(10));
        // Human goes first
        mInfoTextView.setText(R.string.first_human);
    } // End of startNewGame

    // Handles clicks on the game board buttons
    private class ButtonClickListener implements View.OnClickListener {
        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }
        public void onClick(View view) {
            if(location == 9){
                startNewGame();
            }else if (location == 10) {
                humanVictories = 0;
                androidVictories = 0;
                ties = 0;
                startNewGame();
            }else if (mBoardButtons[location].isEnabled()) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1){
                    mInfoTextView.setText(R.string.result_tie);
                    ties = ties + 1;
                    mGameOver = true;
                    endGame();
                }else if (winner == 2){
                    mInfoTextView.setText(R.string.result_human_wins);
                    humanVictories = humanVictories + 1;
                    mGameOver = true;
                    endGame();
                }else{
                    mInfoTextView.setText(R.string.result_computer_wins);
                    androidVictories = androidVictories + 1;
                    mGameOver = true;
                    endGame();
                }
            }
            mHumanVictories.setText(Integer.toString(humanVictories));
            mAndroidVictories.setText(Integer.toString(androidVictories));
            mTies.setText(Integer.toString(ties));
        }
    }
    private void setMove(char player, int location) {
        if(!mGameOver){
            mGame.setMove(player, location);
            mBoardButtons[location].setEnabled(false);
            mBoardButtons[location].setText(String.valueOf(player));
            if (player == TicTacToeGame.HUMAN_PLAYER)
                mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
            else
                mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
        }
    }
    private void endGame(){
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setEnabled(false);
        }
    }
}
