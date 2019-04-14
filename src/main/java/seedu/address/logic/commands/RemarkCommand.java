package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.model.person.Remark;

/**
 * Edits the remark for a person specified in the INDEX.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the remark of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_REMARK + "TEXT] "
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_REMARK + "Likes to drink coffee.";

    public static final String MESSAGE_REMARK_NOT_EDITED = "Remark's TEXT hasn't changed.";
    public static final String MESSAGE_REMARK_UPDATED_SUCCESS = "Remark updated: %1$s";
    public static final String MESSAGE_REMARK_REMOVED_SUCCESS = "Remark has been removed.\n Updated %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String VALIDATION_REGEX = "^$";

    private final Index index;
    private final RemarkPersonDescriptor remarkPersonDescriptor;

    /**
     * @param index of the person in the filtered person list to remark
     * @param remarkPersonDescriptor details to remark the person with
     */
    public RemarkCommand(Index index, RemarkPersonDescriptor remarkPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(remarkPersonDescriptor);

        this.index = index;
        this.remarkPersonDescriptor = new RemarkPersonDescriptor(remarkPersonDescriptor);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToRemark = lastShownList.get(index.getZeroBased());
        Person remarkedPerson = createRemarkedPerson(personToRemark, remarkPersonDescriptor);

        if (!personToRemark.isSamePerson(remarkedPerson) && model.hasPerson(remarkedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        model.setPerson(personToRemark, remarkedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        model.commitAddressBook();

        if(isRemarkEmpty(remarkedPerson.getRemark().value)){
            return new CommandResult(String.format(MESSAGE_REMARK_REMOVED_SUCCESS, remarkedPerson));
        }else {
            return new CommandResult(String.format(MESSAGE_REMARK_UPDATED_SUCCESS, remarkedPerson));
        }
    }

    /**
     * Creates and returns a {@code Person} with the remark of {@code personToRemark}
     * remarked with {@code remarkPersonDescriptor}.
     */
    private static Person createRemarkedPerson(Person personToRemark, RemarkPersonDescriptor remarkPersonDescriptor) {
        assert personToRemark != null;

        Name name = personToRemark.getName();
        Phone phone = personToRemark.getPhone();
        Email email = personToRemark.getEmail();
        Address address = personToRemark.getAddress();
        Set<Tag> tags = personToRemark.getTags();
        Remark updatedRemark = remarkPersonDescriptor.getRemark().orElse(personToRemark.getRemark());

        return new Person(name, phone, email, address, tags, updatedRemark);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        // state check
        RemarkCommand e = (RemarkCommand) other;
        return index.equals(e.index)
                && remarkPersonDescriptor.equals(e.remarkPersonDescriptor);
    }

    /**
     * Returns true if remark is empty string
     */
    public static boolean isRemarkEmpty(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class RemarkPersonDescriptor {
        private Remark remark;

        public RemarkPersonDescriptor() {}

        /**
         * Copy constructor.
         */
        public RemarkPersonDescriptor(RemarkPersonDescriptor toCopy) {
            setRemark(toCopy.remark);
        }

        /**
         * Returns true if remark is edited.
         */
        public boolean isRemarkEdited() {
            return CollectionUtil.isAnyNonNull(remark);
        }

        public void setRemark(Remark remark) {
            this.remark = remark;
        }

        public Optional<Remark> getRemark() {
            return Optional.ofNullable(remark);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof RemarkPersonDescriptor)) {
                return false;
            }

            // state check
            RemarkPersonDescriptor e = (RemarkPersonDescriptor) other;

            return getRemark().equals(e.getRemark());
        }
    }
}