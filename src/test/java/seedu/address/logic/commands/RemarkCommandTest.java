package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;
import seedu.address.logic.commands.RemarkCommand.RemarkPersonDescriptor;

/**
 * Contains unit tests for RemarkCommand.
 */
public class RemarkCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    private static final String VALID_REMARK = "Test Remark.";
    private static final String EMPTY_REMARK = "";

    @Test
    public void execute_updateRemark_success() {
        Person personToRemark = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person remarkedPerson = new PersonBuilder(personToRemark).withRemark(VALID_REMARK).build();
        RemarkPersonDescriptor remarkPersonDescriptor = new RemarkPersonDescriptor();
        remarkPersonDescriptor.setRemark(remarkedPerson.getRemark());
        RemarkCommand remarkCommand = new RemarkCommand(INDEX_FIRST_PERSON, remarkPersonDescriptor);

        String expectedMessage = String.format(RemarkCommand.MESSAGE_REMARK_UPDATED_SUCCESS, remarkedPerson);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToRemark,remarkedPerson);
        expectedModel.commitAddressBook();

        assertCommandSuccess(remarkCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeRemark_sucess() {
        Person personToRemark = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person remarkedPerson = new PersonBuilder(personToRemark).withRemark(EMPTY_REMARK).build();
        RemarkPersonDescriptor remarkPersonDescriptor = new RemarkPersonDescriptor();
        remarkPersonDescriptor.setRemark(remarkedPerson.getRemark());
        RemarkCommand remarkCommand = new RemarkCommand(INDEX_FIRST_PERSON, remarkPersonDescriptor);

        String expectedMessage = String.format(RemarkCommand.MESSAGE_REMARK_REMOVED_SUCCESS, remarkedPerson);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(personToRemark,remarkedPerson);
        expectedModel.commitAddressBook();

        assertCommandSuccess(remarkCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndex_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        RemarkPersonDescriptor remarkPersonDescriptor = new RemarkPersonDescriptor();
        RemarkCommand remarkCommand = new RemarkCommand(outOfBoundIndex, remarkPersonDescriptor);
        assertCommandFailure(remarkCommand, model, commandHistory, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }
}