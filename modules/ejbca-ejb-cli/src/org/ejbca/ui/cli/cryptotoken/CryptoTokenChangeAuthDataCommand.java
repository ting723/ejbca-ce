package org.ejbca.ui.cli.cryptotoken;

import org.apache.log4j.Logger;
import org.cesecore.authorization.AuthorizationDeniedException;
import org.cesecore.keys.token.CryptoTokenManagementSessionRemote;
import org.cesecore.keys.token.CryptoTokenOfflineException;
import org.cesecore.util.EjbRemoteHelper;
import org.ejbca.ui.cli.infrastructure.command.CommandResult;
import org.ejbca.ui.cli.infrastructure.parameter.Parameter;
import org.ejbca.ui.cli.infrastructure.parameter.ParameterContainer;
import org.ejbca.ui.cli.infrastructure.parameter.enums.MandatoryMode;
import org.ejbca.ui.cli.infrastructure.parameter.enums.ParameterMode;
import org.ejbca.ui.cli.infrastructure.parameter.enums.StandaloneMode;

public class CryptoTokenChangeAuthDataCommand extends BaseCryptoTokenCommand {

    private static final Logger log = Logger.getLogger(CryptoTokenChangeAuthDataCommand.class);

    private static final String ALIAS = "--alias";
    private static final String KAKTOKENKEYALIAS = "--kak-tokenkey-alias";
    private static final String PADDING_SCHEME = "--pading-scheme";
    private static final String NEW_KAK_TOKEN = "--new-kak-token";
    
    {
        registerParameter(
                new Parameter(ALIAS, "Alias", MandatoryMode.MANDATORY, StandaloneMode.ALLOW, ParameterMode.ARGUMENT, "HSM Key pair alias"));
        registerParameter(
                new Parameter(KAKTOKENKEYALIAS, "kak token key alias ", MandatoryMode.MANDATORY, StandaloneMode.ALLOW,
                ParameterMode.ARGUMENT, "Alias of the kak token key"));
        registerParameter(
                new Parameter(PADDING_SCHEME, "Padding Scheme ", MandatoryMode.MANDATORY, StandaloneMode.ALLOW, ParameterMode.ARGUMENT,
                "Padding scheme used to sign the hash of new kak with, must be same as the old padding scheme. "));
        registerParameter(
                new Parameter(NEW_KAK_TOKEN, "kak token name ", MandatoryMode.MANDATORY, StandaloneMode.ALLOW,
                ParameterMode.ARGUMENT, "Token representing the new kak to be used."));
    }

    @Override
    public String getMainCommand() {
        return "changeauthdata";
    }

    @Override
    public String getCommandDescription() {
        return "Changes authentication data for a CP5 HSM key.";
    }

    @Override
    public CommandResult executeCommand(Integer cryptoTokenId, ParameterContainer parameters)
            throws AuthorizationDeniedException, CryptoTokenOfflineException {
        final String alias = parameters.get(ALIAS);
        final String kakTokenKeyAlias = parameters.get(KAKTOKENKEYALIAS);
        final String selectedPaddingScheme = parameters.get(PADDING_SCHEME);
        try {
            final CryptoTokenManagementSessionRemote cryptoTokenManagementSession = EjbRemoteHelper.INSTANCE
                    .getRemoteSession(CryptoTokenManagementSessionRemote.class);
            final int kakTokenId = cryptoTokenManagementSession.getIdFromName(parameters.get(NEW_KAK_TOKEN));

            cryptoTokenManagementSession.changeAuthData(getAdmin(), cryptoTokenId, alias, kakTokenId, kakTokenKeyAlias, selectedPaddingScheme);
            return CommandResult.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error("Changing authentication data for the key  "  + alias + " failed : " + e);
            return CommandResult.FUNCTIONAL_FAILURE;
        }
    }

    @Override
    protected Logger getLogger() {
        return log;
    }

    @Override
    public String getFullHelpText() {
        return getCommandDescription();
    }

}