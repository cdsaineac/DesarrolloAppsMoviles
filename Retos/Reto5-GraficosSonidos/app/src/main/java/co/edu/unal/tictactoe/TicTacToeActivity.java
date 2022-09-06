package co.edu.unal.tictactoe;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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

    private BoardView mBoardView;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;


    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.retrosound);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.robotsound);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

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
        mGame = new TicTacToeGame();

        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanVictories = (TextView) findViewById(R.id.Human_Victories);
        mHumanVictories.setText("0");
        mAndroidVictories = (TextView) findViewById(R.id.Android_Victories);
        mAndroidVictories.setText("0");
        mTies = (TextView) findViewById(R.id.Ties);
        mTies.setText("0");

        mGame = new TicTacToeGame();
        startNewGame();
    }


    // Set up the game board.
    private void startNewGame() {
        mGameOver = false;
        mGame.clearBoard();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.invalidate(); // Redraw the board
        // Listen for touches on the board
        mBoardView.setOnTouchListener(mTouchListener);

        // Human goes first
        mInfoTextView.setText(R.string.first_human);
    } // End of startNewGame

    // Handles clicks on the game board buttons

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            System.out.println(pos);
            if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
            // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            int move = mGame.getComputerMove();
                            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                            int winner = mGame.checkForWinner();
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
                            mHumanVictories.setText(Integer.toString(humanVictories));
                            mAndroidVictories.setText(Integer.toString(androidVictories));
                            mTies.setText(Integer.toString(ties));
                        }
                    }, 3000);

                }
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            if(player == TicTacToeGame.COMPUTER_PLAYER){
                mComputerMediaPlayer.start();
            }else{
                mHumanMediaPlayer.start();
            }
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }
    private void endGame(){
        System.out.println("emdGame");
    }

}
