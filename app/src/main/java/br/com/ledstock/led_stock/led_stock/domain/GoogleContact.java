package br.com.ledstock.led_stock.led_stock.domain;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import br.com.ledstock.led_stock.R;


/**
 * Created by Gustavo on 24/08/2016.
 */
public class GoogleContact {

    private final String TAG = "LS_CONTACT_GMAIL";
    private String RAWCONTACTINSERTED = null;

    public String InsertContactAccountGmail(Context context, String[] Values) {

        String ACCOUNT_GMAIL = "ledstockagenda@gmail.com";
        String ACCOUNT_TYPE = "com.google";

        boolean containaccount = false;

        String name = Values[0];
        String phone = Values[1];
        String email = Values[2];
        String phone2 = Values[3];
        String email2 = Values[4];
        String address = Values[5];


        AccountManager manager = AccountManager.get(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(context, "Você não tem permissão para cadastrar contatos no Gmail !", Toast.LENGTH_SHORT).show();
           return "NOT_INSERTED";
        }

        Account[] accounts = manager.getAccountsByType("com.google");

        Log.e("TESTE", "Permissão Garantida");

        for (Account account : accounts) {
            //Verifica se o celular tem a conta do Gmail
            if (account.name.equals(ACCOUNT_GMAIL)) {
                containaccount = true;
                Log.e("TESTE", "Celular tem a conta do Gmail");
            }else{
                Log.e("TESTE", "Celular não tem a conta do Gmail");
            }
        }

        //Caso possua a conta, insere o contato na conta
        if (containaccount) {
             /*
             * Prepares the batch operation for inserting a new raw contact and its data. Even if
             * the Contacts Provider does not have any data for this person, you can't add a Contact,
             * only a raw contact. The Contacts Provider will then add a Contact automatically.
             */
            // Creates a new array of ContentProviderOperation objects.
            ArrayList<ContentProviderOperation> ops =
                    new ArrayList<ContentProviderOperation>();

            // ContentProviderOperation.Builder op =
            //        ContentProviderOperation.new

    /*
     * Creates a new raw contact with its account type (server type) and account name
     * (user's account). Remember that the display name is not stored in this row, but in a
     * StructuredName data row. No other data is required.
     */
            ContentProviderOperation.Builder op =
                    ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, ACCOUNT_TYPE)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, ACCOUNT_GMAIL);

            // Builds the operation and adds it to the array of operations
            ops.add(op.build());


            // Creates the display name for the new raw contact, as a StructuredName data row.
            op =
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            /*
             * withValueBackReference sets the value of the first argument to the value of
             * the ContentProviderResult indexed by the second argument. In this particular
             * call, the raw contact ID column of the StructuredName data row is set to the
             * value of the result returned by the first operation, which is the one that
             * actually adds the raw contact row.
             */
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                            // Sets the data row's MIME type to StructuredName
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)

                            // Sets the data row's display name to the name in the UI.
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);

            // Builds the operation and adds it to the array of operations
            ops.add(op.build());


            // Inserts the specified Address data row
            op =
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            /*
             * Sets the value of the raw contact id column to the new raw contact ID returned
             * by the first operation in the batch.
             */
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                            // Sets the data row's MIME type to Phone
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)

                            // Sets the phone number and type
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, 2);

            // Builds the operation and adds it to the array of operations
            ops.add(op.build());

            // Inserts the specified phone number and type as a Phone data row
            op =
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            /*
             * Sets the value of the raw contact id column to the new raw contact ID returned
             * by the first operation in the batch.
             */
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                            // Sets the data row's MIME type to Phone
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)

                            // Sets the phone number and type
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 3);

            // Builds the operation and adds it to the array of operations
            ops.add(op.build());

            // Inserts the specified phone number and type as a Phone data row
            op =
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            /*
             * Sets the value of the raw contact id column to the new raw contact ID returned
             * by the first operation in the batch.
             */
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                            // Sets the data row's MIME type to Phone
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)

                            // Sets the phone number and type
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone2)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, 7);

            // Builds the operation and adds it to the array of operations
            ops.add(op.build());

            // Inserts the specified email and type as a Phone data row
            op =
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            /*
             * Sets the value of the raw contact id column to the new raw contact ID returned
             * by the first operation in the batch.
             */
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                            // Sets the data row's MIME type to Email
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)

                            // Sets the email address and type
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, 2);


            // Builds the operation and adds it to the array of operations
            ops.add(op.build());

            // Inserts the specified email and type as a Phone data row
            op =
                    ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            /*
             * Sets the value of the raw contact id column to the new raw contact ID returned
             * by the first operation in the batch.
             */
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                            // Sets the data row's MIME type to Email
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)

                            // Sets the email address and type
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, email2)
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, 3);

        /*
         * Demonstrates a yield point. At the end of this insert, the batch operation's thread
         * will yield priority to other threads. Use after every set of operations that affect a
         * single contact, to avoid degrading performance.
         */
            op.withYieldAllowed(true);

            // Builds the operation and adds it to the array of operations
            ops.add(op.build());

            // Ask the Contacts Provider to create a new contact
            // Log.d(TAG,"Selected account: " + mSelectedAccount.getName() + " (" +
            //         mSelectedAccount.getType() + ")");
            Log.d(TAG, "Creating contact: " + name);

        /*
         * Applies the array of ContentProviderOperation objects in batch. The results are
         * discarded.
         */

            try {

                ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

                Uri myContactUri = results[0].uri;
                int lastSlash = myContactUri.toString().lastIndexOf("/");
                int length = myContactUri.toString().length();
                String RAW = (String) myContactUri.toString().subSequence(lastSlash + 1, length);

                Log.d(TAG, "Criado contato com RAW_CONTACT_ID = " + RAW);

                String CONTACT_ID = "";
                Uri uri = ContactsContract.Data.CONTENT_URI;
                Cursor c = context.getContentResolver().query(uri, new String[]{ContactsContract.Data.CONTACT_ID}, ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[]{RAW}, null);

                if (c != null) {
                    c.moveToFirst();
                    CONTACT_ID = c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                    c.close();
                    // Log.d(TAG, "Este é o CONTACT_ID a ser apagado: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID)));
                } else {
                    Log.d(TAG, "Cursor C é nullo");
                }

                Log.d(TAG, "Criado contato com CONTACT_ID = " + CONTACT_ID);

                return RAW;

            } catch (Exception e) {

                // Display a warning
                Context ctx = context.getApplicationContext();

                CharSequence txt = context.getString(R.string.contactCreationFailure);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(ctx, txt, duration);
                toast.show();

                // Log exception
                Log.e(TAG, "Exception encountered while inserting contact: " + e);

                return "NOT_INSERTED";
            }
        } else {
            Log.d(TAG, "Conta não disponível !");
            return "NOT_INSERTED";
        }

        //UpdateContactByID(context, "130", new String[]{"MAOEW", "(11) 7777-7777", "email@email.com", "(99) 2554-2554", "email2@segundoemail2.com", "Rua do contato padrao, 123"}, new String[]{"MAOEW", "(11) 7777-7777", "email@email.com", "(99) 2554-2554", "email2@segundoemail2.com", "Rua Editada, 145"});

    }

    public boolean UpdateContactByID(final Context context, String RAW_CONTACT_ID, String[] OldValues, String[] NewValues) {

        /*
        String Values[] = {Nome, Phone, Email, Phone2, Email2, Endereço}
         */

        //Se o Raw Contact for nulo ou vazio, não executa o update
        if ((RAW_CONTACT_ID != null) && (!RAW_CONTACT_ID.equals(""))) {

            ArrayList<ContentProviderOperation> ops_update = new ArrayList<ContentProviderOperation>();


            if (OldValues[0].equals(NewValues[0])) {
                ops_update.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.DATA1 + "=?", new String[]{RAW_CONTACT_ID, OldValues[0]})

                        // Sets the email address and type
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, NewValues[0])
                        .build());
            }

            for (int i = 1; i <= 5; i++) {

                if (OldValues[i].equals(NewValues[i])) {
                    ops_update.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                            .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.DATA1 + "=?", new String[]{RAW_CONTACT_ID, OldValues[i]})

                            // Sets the email address and type
                            .withValue(ContactsContract.Data.DATA1, NewValues[i])
                            .build());
                }
            }

            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops_update);
                Log.d(TAG, "CONTATO EDITADO COM SUCESSO !");
                return true;
            } catch (Exception e) {

                // Log exception
                Log.e(TAG, "Exception encountered while updating contact: " + e);
                return false;
            }

        } else {
            return false;
        }
    }

    public boolean DeleteContact(Context context, String Raw_Contact_ID) {

        //Se o Raw Contact for nulo ou vazio, não executa o update
        if ((Raw_Contact_ID != null) && (!Raw_Contact_ID.equals(""))) {
            String CONTACT_ID = "";
            Uri uri = ContactsContract.Data.CONTENT_URI;
            Cursor c = context.getContentResolver().query(uri, new String[]{ContactsContract.Data.CONTACT_ID}, ContactsContract.Data.RAW_CONTACT_ID + "=?", new String[]{Raw_Contact_ID}, null);

            if (c != null) {
                c.moveToFirst();
                CONTACT_ID = c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID));
                Log.d(TAG, "Este é o CONTACT_ID a ser apagado: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID)));
                c.close();
            } else {
                Log.d(TAG, "Cursor C é nullo");
            }

            if (CONTACT_ID.equals("")) {

                ArrayList<ContentProviderOperation> ops_delete = new ArrayList<ContentProviderOperation>();

                ops_delete.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=?", new String[]{CONTACT_ID})
                        .build());

                try {
                    context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops_delete);
                    Log.d(TAG, "Contato Excluido");
                    return true;
                } catch (Exception e) {

                    // Log exception
                    Log.e(TAG, "Exception encountered while Delete contact: " + e);
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void AtualizaLista(final Context context) {

        new Thread(new Runnable() {
            public void run() {

                Uri uri = ContactsContract.Data.CONTENT_URI;
                Cursor c = context.getContentResolver().query(uri, null, null, null, null);

                if (c != null) {

                    do {
                        Log.d(TAG, "************************************************************************");
                        Log.d(TAG, "Nome: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DISPLAY_NAME)));
                        Log.d(TAG, "Data._ID: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data._ID)));
                        Log.d(TAG, "CONTACT_ID: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID)));
                        Log.d(TAG, "RAW_CONTACT_ID: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.RAW_CONTACT_ID)));
                        Log.d(TAG, "Data.DATA1: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA1)));
                        Log.d(TAG, "Data.DATA2: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA2)));
                        Log.d(TAG, "Data.DATA3: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA3)));
                        Log.d(TAG, "Data.DATA3: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA4)));
                        Log.d(TAG, "Data.DATA5: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA5)));
                        Log.d(TAG, "Data.DATA6: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA6)));
                        Log.d(TAG, "Data.DATA7: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA7)));
                        Log.d(TAG, "Data.DATA8: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA8)));
                        Log.d(TAG, "Data.DATA9: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA9)));
                        Log.d(TAG, "Data.DATA10: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA10)));
                        Log.d(TAG, "Data.DATA11: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA11)));
                        Log.d(TAG, "Data.DATA12: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA12)));
                        Log.d(TAG, "Data.DATA13: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA13)));
                        Log.d(TAG, "Data.DATA14: " + c.getString(c.getColumnIndexOrThrow(ContactsContract.Data.DATA14)));
                        Log.d(TAG, "Data.DATA15: " + c.getBlob(c.getColumnIndexOrThrow(ContactsContract.Data.DATA15)));
                        Log.d(TAG, "************************************************************************");
                    } while (c.moveToNext());
                    c.close();
                } else {
                    Log.d(TAG, "c é nullo");
                }
            }
        }).start();
    }
}


// ContentValues values2 = new ContentValues();
// values2.put(ContactsContract.CommonDataKinds.Email.DATA, "teste2@teste2.com.br");


//Uri uri2 = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, 154);
//int count = context.getContentResolver().update(uri2, values2, null, null);
// Log.d(TAG, "Contato Editado -> " + String.valueOf(count));


//String baseSelection = Data.CONTACT_ID + "=?";
// String[] baseSelectionArgs = new String[]{String.valueOf(idContact)};


//String selection = baseSelection + " AND " + Data.MIMETYPE + "=?";
// String[] selectionArgs = new String[]{baseSelectionArgs[0], StructuredName.CONTENT_ITEM_TYPE};
//ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
//         .withSelection(selection, selectionArgs)
//        .withValue(StructuredName.GIVEN_NAME, firstName)
//        .withValue(StructuredName.FAMILY_NAME, surname).build());


// String selection = baseSelection + " AND " + Data.MIMETYPE + "=? AND " + String.valueOf(Phone.TYPE) + " = ? ";

// String[] selectionArgs = new String[]{baseSelectionArgs[0], Phone.CONTENT_ITEM_TYPE, String.valueOf(Phone.TYPE_MOBILE)};


//Remove the Contact
//Uri uri3 = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, 150);
//int count = context.getContentResolver().delete(uri3,null,null);
// Log.d(TAG, "Contato apagado -> " + String.valueOf(count));
        /*

        ArrayList<ContentProviderOperation> ops2 =
                new ArrayList<ContentProviderOperation>();

        ops2.add(ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI)
                //        .withSelection(ContactsContract.Data.CONTACT_ID + "=?", new String[]{RAWCONTACTINSERTED})
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, ACCOUNT_TYPE)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, ACCOUNT_GMAIL).build());

        ops2.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Contacts._ID + "=?", new String[]{"137"})
                // Sets the data row's MIME type to Email
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)

                // Sets the email address and type
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "somebody@somebody.com")
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, 2)
                .build());

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops2);
            Log.d(TAG, "Conseguiu Editar");
        } catch (Exception e) {

        }


/*
// Creates a new intent for sending to the device's contacts application
        Intent insertIntent = new Intent(ContactsContract.Intents.Insert.ACTION);

// Sets the MIME type to the one expected by the insertion activity
        insertIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

// Sets the new contact name
        insertIntent.putExtra(ContactsContract.Intents.Insert.NAME, "LEDSTOCK2");

/*
 * Demonstrates adding data rows as an array list associated with the DATA key
 */
/*
// Defines an array list to contain the ContentValues objects for each row
        ArrayList<ContentValues> contactData = new ArrayList<ContentValues>();
*/

/*
 * Defines the raw contact row
 */
/*
// Sets up the row as a ContentValues object
        ContentValues rawContactRow = new ContentValues();

// Adds the account type and name to the row
        rawContactRow.put(ContactsContract.RawContacts.ACCOUNT_TYPE, ACCOUNT_TYPE);
        rawContactRow.put(ContactsContract.RawContacts.ACCOUNT_NAME, ACCOUNT_GMAIL);

// Adds the row to the array
        contactData.add(rawContactRow);
*/
/*
 * Sets up the phone number data row
 */
/*
// Sets up the row as a ContentValues object
        ContentValues phoneRow = new ContentValues();

// Specifies the MIME type for this data row (all data rows must be marked by their type)
        phoneRow.put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        );

// Adds the phone number and its type to the row
        phoneRow.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "000-0000");

// Adds the row to the array
        contactData.add(phoneRow);
*/
/*
 * Sets up the email data row
 */
/*
// Sets up the row as a ContentValues object
        ContentValues emailRow = new ContentValues();

// Specifies the MIME type for this data row (all data rows must be marked by their type)
        emailRow.put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
        );

// Adds the email address and its type to the row
        emailRow.put(ContactsContract.CommonDataKinds.Email.DATA, "somebody@somebody.com");

// Adds the row to the array
        contactData.add(emailRow);
*/
/*
 * Adds the array to the intent's extras. It must be a parcelable object in order to
 * travel between processes. The device's contacts app expects its key to be
 * Intents.Insert.DATA
 */
        /*
        insertIntent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.EMAIL, contactData);

// Send out the intent to start the device's contacts app in its add contact activity.
        context.startActivity(insertIntent);

*/

        /*
        ArrayList<ContentProviderOperation> ops =
                new ArrayList<ContentProviderOperation>();

        ContentProviderOperation.Builder op =
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google")
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "gustavo.aveiro03@gmail.com");

        // Builds the operation and adds it to the array of operations
        ops.add(op.build());



        ContentValues values = new ContentValues();
        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, "0");
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "1-800-GOOG-411");
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(ContactsContract.CommonDataKinds.Phone.LABEL, "free directory assistance");
        Uri dataUri = context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);


        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        String gmail = null;

        for(Account account: list)
        {
            if(account.type.equalsIgnoreCase("com.google"))
            {
                gmail = account.name;
                Log.d("GMAILACCOUNT", gmail);
            }
        }

        AccountManager manager;
        manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();

        ContentValues values = new ContentValues();
        values.put(ContactsContract.Contacts.Data.RAW_CONTACT_ID, "0");
        values.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, "1-800-GOOG-411");
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
        values.put(ContactsContract.CommonDataKinds.Phone.LABEL, "free directory assistance");
        Uri dataUri = context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);*/