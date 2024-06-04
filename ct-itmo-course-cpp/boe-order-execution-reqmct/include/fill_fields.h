#include "codec.h"
#include "convert_functions.h"

inline ExecutionDetails fill_order_execution(const std::vector<unsigned char> & message)
{
    ExecutionDetails exec_details;
#define NAMESPACE fields_order_execution
#define RETURN_VALUE exec_details
#define VARIABLES_LOCATION "fields_order_execution.inl"
#define OPTIONAL_VARIABLES_LOCATION "optional_fields_order_execution.inl"
#include "fill_fields.inl"
    return exec_details;
}

inline RestatementDetails fill_order_details(const std::vector<unsigned char> & message)
{
    RestatementDetails restatement_details;
#define NAMESPACE fields_order_restatement
#define RETURN_VALUE restatement_details
#define VARIABLES_LOCATION "fields_order_restatement.inl"
#define OPTIONAL_VARIABLES_LOCATION "optional_fields_order_restatement.inl"
#include "fill_fields.inl"
    return restatement_details;
}
