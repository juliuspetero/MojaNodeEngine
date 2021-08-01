package com.mojagap.mojanode.service.recipient;

import com.mojagap.mojanode.dto.ActionResponse;
import com.mojagap.mojanode.dto.recipient.RecipientBankDetailDto;
import com.mojagap.mojanode.dto.recipient.RecipientCsvDto;
import com.mojagap.mojanode.dto.recipient.RecipientDto;
import com.mojagap.mojanode.infrastructure.AppContext;
import com.mojagap.mojanode.infrastructure.ErrorMessages;
import com.mojagap.mojanode.infrastructure.PowerValidator;
import com.mojagap.mojanode.infrastructure.exception.BadRequestException;
import com.mojagap.mojanode.infrastructure.utility.CsvUtil;
import com.mojagap.mojanode.model.account.Account;
import com.mojagap.mojanode.model.account.AccountType;
import com.mojagap.mojanode.model.branch.Branch;
import com.mojagap.mojanode.model.common.AuditEntity;
import com.mojagap.mojanode.model.common.IdTypeEnum;
import com.mojagap.mojanode.model.company.Company;
import com.mojagap.mojanode.model.recipient.Recipient;
import com.mojagap.mojanode.model.recipient.RecipientBankDetail;
import com.mojagap.mojanode.model.user.AppUser;
import com.mojagap.mojanode.repository.branch.BranchRepository;
import com.mojagap.mojanode.repository.company.CompanyRepository;
import com.mojagap.mojanode.repository.recipient.RecipientBankDetailRepository;
import com.mojagap.mojanode.repository.recipient.RecipientRepository;
import com.mojagap.mojanode.service.recipient.handler.RecipientCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipientCommandService implements RecipientCommandHandler {
    private final RecipientRepository recipientRepository;
    private final RecipientBankDetailRepository recipientBankDetailRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;

    @Autowired
    public RecipientCommandService(RecipientRepository recipientRepository, RecipientBankDetailRepository recipientBankDetailRepository, CompanyRepository companyRepository, BranchRepository branchRepository) {
        this.recipientRepository = recipientRepository;
        this.recipientBankDetailRepository = recipientBankDetailRepository;
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public ActionResponse createRecipient(RecipientDto recipientDto) {
        recipientDto.isValid();
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = loggedInUser.getAccount();
        AccountType accountType = account.getAccountType();
        PowerValidator.isPermittedAccountType(accountType, AccountType.INDIVIDUAL, AccountType.COMPANY);
        Recipient recipient = recipientDto.toRecipientEntity();
        AppContext.stamp(recipient);
        RecipientBankDetailDto recipientBankDetailDto = recipientDto.getRecipientBankDetail();
        recipientBankDetailDto.isValid();
        RecipientBankDetail recipientBankDetail = recipientBankDetailDto.toRecipientBankDetailEntity();
        AppContext.stamp(recipientBankDetail);
        recipient.setRecipientBankDetail(recipientBankDetail);
        recipient.setAccount(account);
        if (AccountType.COMPANY.equals(accountType)) {
            updateRecipientDetails(recipientDto, recipient);
        }
        recipient = recipientRepository.save(recipient);
        return new ActionResponse(recipient.getId());
    }

    @Override
    @Transactional
    public ActionResponse createRecipientViaCsv(MultipartFile multipartFile) {
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = loggedInUser.getAccount();
        AccountType accountType = account.getAccountType();
        PowerValidator.isPermittedAccountType(accountType, AccountType.INDIVIDUAL, AccountType.COMPANY);
        List<RecipientCsvDto> recipientCsvDtos = CsvUtil.parseMultipartCsv(multipartFile, RecipientCsvDto.class);
        List<Recipient> recipients = recipientCsvDtos.stream().map(dto -> toRecipientEntity(dto, loggedInUser)).collect(Collectors.toList());
        recipientRepository.saveAll(recipients);
        return new ActionResponse("Uploaded the file successfully: " + multipartFile.getOriginalFilename());
    }

    private Recipient toRecipientEntity(RecipientCsvDto recipientCsvDto, AppUser loggedInUser) {
        recipientCsvDto.isValid();
        Account account = loggedInUser.getAccount();
        AccountType accountType = account.getAccountType();
        Recipient recipient = new Recipient();
        recipient.setFirstName(recipientCsvDto.getFirstName());
        recipient.setLastName(recipientCsvDto.getLastName());
        recipient.setDateOfBirth(recipientCsvDto.getDateOfBirth());
        recipient.setIdTypeEnum(IdTypeEnum.valueOf(recipientCsvDto.getIdTypeEnum()));
        recipient.setIdNumber(recipientCsvDto.getIdNumber());
        recipient.setAddress(recipientCsvDto.getAddress());
        recipient.setEmail(recipientCsvDto.getEmail());
        recipient.setPhoneNumber(recipientCsvDto.getPhoneNumber());
        recipient.setAccount(loggedInUser.getAccount());
        AppContext.stamp(recipient);
        if (AccountType.COMPANY.equals(accountType)) {
            Company company = companyRepository.getById(loggedInUser.getCompany().getId());
            Branch branch = branchRepository.getById(loggedInUser.getBranch().getId());
            recipient.setCompany(company);
            recipient.setBranch(branch);
        }
        RecipientBankDetail recipientBankDetail = new RecipientBankDetail();
        recipientBankDetail.setBankName(recipientCsvDto.getBankName());
        recipientBankDetail.setAccountName(recipientCsvDto.getAccountName());
        recipientBankDetail.setAccountNumber(recipientCsvDto.getAccountNumber());
        recipientBankDetail.setBranchName(recipientCsvDto.getBranchName());
        recipientBankDetail.setSwiftCode(recipientCsvDto.getSwiftCode());
        AppContext.stamp(recipientBankDetail);
        recipient.setRecipientBankDetail(recipientBankDetail);
        return recipient;
    }

    private void updateRecipientDetails(RecipientDto recipientDto, Recipient recipient) {
        PowerValidator.notNull(recipientDto.getCompany().getId(), String.format(ErrorMessages.ENTITY_REQUIRED, "company ID"));
        Company company = companyRepository.findCompanyById(recipientDto.getCompany().getId())
                .orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Company.class.getSimpleName(), "ID")));
        List<Integer> loggedInUserCompanies = AppContext.getCompaniesOfLoggedInUser().stream().map(Company::getId).collect(Collectors.toList());
        if (!loggedInUserCompanies.contains(recipientDto.getCompany().getId())) {
            PowerValidator.throwBadRequestException(ErrorMessages.CANNOT_CREATE_USER_UNDER_SUB_COMPANY);
        }
        recipient.setCompany(company);
        PowerValidator.notNull(recipientDto.getBranch().getId(), String.format(ErrorMessages.ENTITY_REQUIRED, "branch ID"));
        Branch branch = branchRepository.findById(recipientDto.getBranch().getId())
                .orElseThrow(() -> new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, Branch.class.getSimpleName(), "ID")));
        List<Integer> loggedInUserBranches = AppContext.getBranchesOfLoggedInUser().stream().map(Branch::getId).collect(Collectors.toList());
        if (!loggedInUserBranches.contains(recipientDto.getBranch().getId())) {
            PowerValidator.throwBadRequestException(ErrorMessages.CANNOT_CREATE_USER_IN_BRANCH);
        }
        recipient.setBranch(branch);
    }

    @Override
    @Transactional
    public ActionResponse updateRecipient(RecipientDto recipientDto, Integer id) {
        Recipient recipient = recipientRepository.findById(id).orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Recipient", "ID")));
        PowerValidator.isTrue(AuditEntity.RecordStatus.ACTIVE.equals(recipient.getRecordStatus()), String.format(ErrorMessages.CANNOT_UPDATE_RECORD_IN_STATUS, "Recipient", recipient.getRecordStatus()));
        List<Integer> loggedInUserBranches = AppContext.getBranchesOfLoggedInUser().stream().map(Branch::getId).collect(Collectors.toList());
        if (!loggedInUserBranches.contains(recipient.getBranch().getId())) {
            PowerValidator.throwBadRequestException(ErrorMessages.CANNOT_CREATE_OR_UPDATE_RECIPIENT_IN_BRANCH);
        }
        recipientDto.isValid();
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = loggedInUser.getAccount();
        AccountType accountType = account.getAccountType();
        PowerValidator.isPermittedAccountType(accountType, AccountType.INDIVIDUAL, AccountType.COMPANY);
        fromRecipientDto(recipientDto, recipient);
        AppContext.stamp(recipient);
        RecipientBankDetail recipientBankDetail = recipient.getRecipientBankDetail();
        RecipientBankDetailDto recipientBankDetailDto = recipientDto.getRecipientBankDetail();
        recipientBankDetailDto.isValid();
        fromRecipientBankDetailDto(recipientBankDetailDto, recipientBankDetail);
        AppContext.stamp(recipientBankDetail);
        recipient.setRecipientBankDetail(recipientBankDetail);
        if (AccountType.COMPANY.equals(accountType)) {
            updateRecipientDetails(recipientDto, recipient);
        }
        recipient = recipientRepository.save(recipient);
        return new ActionResponse(recipient.getId());
    }

    private void fromRecipientDto(RecipientDto recipientDto, Recipient recipient) {
        recipient.setFirstName(recipientDto.getFirstName());
        recipient.setLastName(recipientDto.getLastName());
        recipient.setDateOfBirth(recipientDto.getDateOfBirth());
        recipient.setIdNumber(recipientDto.getIdNumber());
        recipient.setIdTypeEnum(IdTypeEnum.valueOf(recipientDto.getIdTypeEnum()));
        recipient.setAddress(recipientDto.getAddress());
        recipient.setEmail(recipientDto.getEmail());
        recipient.setPhoneNumber(recipientDto.getPhoneNumber());
    }

    private void fromRecipientBankDetailDto(RecipientBankDetailDto recipientBankDetailDto, RecipientBankDetail recipientBankDetail) {
        recipientBankDetail.setBankName(recipientBankDetailDto.getBankName());
        recipientBankDetail.setAccountName(recipientBankDetailDto.getAccountName());
        recipientBankDetail.setAccountNumber(recipientBankDetailDto.getAccountNumber());
        recipientBankDetail.setBranchName(recipientBankDetailDto.getBranchName());
        recipientBankDetail.setSwiftCode(recipientBankDetailDto.getSwiftCode());
    }

    @Override
    @Transactional
    public ActionResponse closeRecipient(Integer id) {
        AppUser loggedInUser = AppContext.getLoggedInUser();
        Account account = loggedInUser.getAccount();
        PowerValidator.isPermittedAccountType(account.getAccountType(), AccountType.COMPANY, AccountType.INDIVIDUAL);
        Recipient recipient = recipientRepository.findByIdAndAccountId(id, account.getId()).orElseThrow(() ->
                new BadRequestException(String.format(ErrorMessages.ENTITY_DOES_NOT_EXISTS, "Recipient", "ID")));
        List<Integer> loggedInUserBranchIds = AppContext.getBranchesOfLoggedInUser().stream().map(Branch::getId).collect(Collectors.toList());
        if (AccountType.COMPANY.equals(account.getAccountType()) && !loggedInUserBranchIds.contains(recipient.getBranch().getId())) {
            PowerValidator.throwBadRequestException(ErrorMessages.NOT_PERMITTED_TO_CLOSE_COMPANY);
        }
        recipient.setRecordStatus(AuditEntity.RecordStatus.CLOSED);
        recipientRepository.save(recipient);
        return new ActionResponse(id);
    }
}
